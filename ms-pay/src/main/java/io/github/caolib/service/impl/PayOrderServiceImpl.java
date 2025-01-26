package io.github.caolib.service.impl;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.client.UserClient;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.PayApplyDTO;
import io.github.caolib.domain.dto.PayOrderFormDTO;
import io.github.caolib.domain.po.PayOrder;
import io.github.caolib.domain.vo.PayOrderVO;
import io.github.caolib.enums.*;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.mapper.PayOrderMapper;
import io.github.caolib.service.IPayOrderService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.UserContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements IPayOrderService {
    private final UserClient userClient;
    private final RabbitTemplate rabbitTemplate;
    private final PayOrderMapper payOrderMapper;

    @Override
    public String applyPayOrder(PayApplyDTO applyDTO) {
        // 幂等性校验
        PayOrder payOrder = checkIdempotent(applyDTO);
        // 返回支付单
        return String.valueOf(payOrder.getId());
    }

    @Override
    public R<PayOrder> createPayOrder(PayApplyDTO applyDTO) {
        // 幂等性校验
        PayOrder payOrder = checkIdempotent(applyDTO);
        // 返回支付单
        return R.ok(payOrder);
    }

    @Override
    @GlobalTransactional
    public void payOrderByBalance(PayOrderFormDTO payOrderFormDTO) {
        String errorMsg = E.ORDER_STATUS_EXCEP;

        // 查询支付单
        PayOrder po = getById(payOrderFormDTO.getId());
        // 判断状态
        if (!PayStatus.WAIT_BUYER_PAY.equalsValue(po.getStatus())) {
            // 订单不是未支付，状态异常
            throw new BizIllegalException(errorMsg);
        }
        // RPC --> 扣减用户余额
        R<String> res = userClient.deductMoney(payOrderFormDTO.getPw(), po.getAmount());
        log.debug("扣减用户余额结果：{}", res);
        if (res.getCode() != 200) {
            throw new BizIllegalException(res.getMsg(), res.getCode());// 扣减余额失败
        }

        // 修改支付单状态
        boolean success = markPayOrderSuccess(payOrderFormDTO.getId(), LocalDateTime.now());
        if (!success) {
            throw new BizIllegalException(errorMsg);
        }

        // MQ --> 修改订单状态为支付成功
        Long orderId = po.getBizOrderNo();
        try {
            rabbitTemplate.convertAndSend(Q.PAY_EXCHANGE, Q.PAY_SUCCESS_KEY, orderId);
            log.debug("<修改订单状态消息发送 --> MQ orderId:{}>", orderId);
        } catch (Exception e) {
            log.error("修改订单状态消息发送 --> MQ 失败，orderId:{}", orderId);
        }
    }

    /**
     * 根据业务订单id查询支付单id
     * @param bizOrderId 业务订单id
     * @return 支付单id
     */
    @Override
    public R<String> getPayOrderId(Long bizOrderId) {
        Long payOrderId = payOrderMapper.getPayOrderId(bizOrderId);
        return R.ok(payOrderId.toString());
    }

    @Override
    public void deleteByUserId(Long userId) {
        payOrderMapper.updatePayStatusByUserId(userId);
    }

    @Override
    public List<PayOrderVO> getUserPayOrders() {
        // 获取用户id
        Long userId = UserContext.getUserId();
        // 查询用户支付单
        List<PayOrder> list = lambdaQuery().eq(PayOrder::getBizUserId, userId).list();

        return BeanUtils.copyList(list, PayOrderVO.class);
    }

    /**
     * 设置订单状态为支付成功
     *
     * @param id          支付单id
     * @param successTime 支付成功时间
     * @return 是否成功
     */
    public boolean markPayOrderSuccess(Long id, LocalDateTime successTime) {
        return lambdaUpdate()
                .set(PayOrder::getStatus, PayStatus.TRADE_SUCCESS.getValue())
                .set(PayOrder::getPaySuccessTime, successTime)
                .eq(PayOrder::getId, id)
                // 支付状态的乐观锁判断
                .in(PayOrder::getStatus, PayStatus.NOT_COMMIT.getValue(), PayStatus.WAIT_BUYER_PAY.getValue())
                .update();
    }


    private PayOrder checkIdempotent(PayApplyDTO applyDTO) {
        // 首先查询支付单
        PayOrder oldOrder = queryByBizOrderNo(applyDTO.getBizOrderNo());
        // 判断是否存在
        if (oldOrder == null) {
            // 不存在支付单，写入新的支付单并返回
            PayOrder payOrder = buildPayOrder(applyDTO);
            payOrder.setPayOrderNo(IdWorker.getId());
            save(payOrder);
            return payOrder;
        }
        // 支付单已经存在，判断是否支付成功
        if (PayStatus.TRADE_SUCCESS.equalsValue(oldOrder.getStatus())) {
            // 订单已经支付
            throw new BizIllegalException(Code.ORDER_ALREADY_PAY);
        }
        // 判断是否已经关闭
        if (PayStatus.TRADE_CLOSED.equalsValue(oldOrder.getStatus())) {
            // 已经关闭，抛出异常
            throw new BizIllegalException(Code.ORDER_IS_CLOSED);
        }
        // 判断支付渠道是否一致
        if (!StringUtils.equals(oldOrder.getPayChannelCode(), applyDTO.getPayChannelCode())) {
            // 支付渠道不一致，需要重置数据，然后重新申请支付单
            PayOrder payOrder = buildPayOrder(applyDTO);
            payOrder.setId(oldOrder.getId());
            payOrder.setQrCodeUrl("");
            updateById(payOrder);
            payOrder.setPayOrderNo(oldOrder.getPayOrderNo());
            return payOrder;
        }
        // 未支付或未提交，直接返回旧数据
        return oldOrder;
    }

    /**
     * 初始化支付单数据
     *
     * @param payApplyDTO 支付申请数据
     */
    private PayOrder buildPayOrder(PayApplyDTO payApplyDTO) {
        // 转换为PO
        PayOrder payOrder = BeanUtils.toBean(payApplyDTO, PayOrder.class);
        // 初始化数据
        payOrder.setPayOverTime(LocalDateTime.now().plusSeconds(Time.TIMEOUT)); // 设置超时时间
        payOrder.setStatus(PayStatus.WAIT_BUYER_PAY.getValue()); // 设置为等待支付状态
        payOrder.setBizUserId(UserContext.getUserId());
        return payOrder;
    }

    /**
     * 根据业务订单号查询支付单
     *
     * @param bizOrderNo 业务订单号
     */
    public PayOrder queryByBizOrderNo(Long bizOrderNo) {
        return lambdaQuery().eq(PayOrder::getBizOrderNo, bizOrderNo).one();
    }
}
