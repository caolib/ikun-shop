package io.github.caolib.service.impl;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.client.UserClient;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.PayApplyDTO;
import io.github.caolib.domain.dto.PayOrderFormDTO;
import io.github.caolib.domain.po.PayOrder;
import io.github.caolib.enums.Code;
import io.github.caolib.enums.E;
import io.github.caolib.enums.PayStatus;
import io.github.caolib.enums.Q;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.mapper.PayOrderMapper;
import io.github.caolib.service.IPayOrderService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements IPayOrderService {
    private final UserClient userClient;
    //private final OrderClient orderClient;
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
    @Transactional
    public void tryPayOrderByBalance(PayOrderFormDTO payOrderFormDTO) {
        String errorMsg = E.ORDER_STATUS_EXCEP;

        // 查询支付单
        PayOrder po = getById(payOrderFormDTO.getId());
        // 判断状态
        if (!PayStatus.WAIT_BUYER_PAY.equalsValue(po.getStatus())) {
            // 订单不是未支付，状态异常
            throw new BizIllegalException(errorMsg);
        }
        // 扣减用户余额
        userClient.deductMoney(payOrderFormDTO.getPw(), po.getAmount());
        // 修改支付单状态
        boolean success = markPayOrderSuccess(payOrderFormDTO.getId(), LocalDateTime.now());
        if (!success) {
            throw new BizIllegalException(errorMsg);
        }
        // todo 发送消息到支付消息队列，修改订单状态
        // orderClient.markOrderPaySuccess(po.getBizOrderNo());
        Long orderId = po.getBizOrderNo();
        try {
            rabbitTemplate.convertAndSend(Q.PAY_EXCHANGE, Q.PAY_SUCCESS_KEY, orderId);
            log.debug("发送了一条修改订单状态消息，orderId:{}", orderId);
        }catch (Exception e){
            log.error("发送修改订单状态消息失败，orderId:{}", orderId);
        }
    }

    @Override
    public R<String> getPayOrderId(Long bizOrderId) {
        Long payOrderId = payOrderMapper.getPayOrderId(bizOrderId);
        return R.ok(payOrderId.toString());
    }

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
        // 1.首先查询支付单
        PayOrder oldOrder = queryByBizOrderNo(applyDTO.getBizOrderNo());
        // 2.判断是否存在
        if (oldOrder == null) {
            // 不存在支付单，说明是第一次，写入新的支付单并返回
            PayOrder payOrder = buildPayOrder(applyDTO);
            payOrder.setPayOrderNo(IdWorker.getId());
            save(payOrder);
            return payOrder;
        }
        // 3.旧单已经存在，判断是否支付成功
        if (PayStatus.TRADE_SUCCESS.equalsValue(oldOrder.getStatus())) {
            // 订单已经支付
            throw new BizIllegalException(Code.ORDER_ALREADY_PAY);
        }
        // 4.旧单已经存在，判断是否已经关闭
        if (PayStatus.TRADE_CLOSED.equalsValue(oldOrder.getStatus())) {
            // 已经关闭，抛出异常
            throw new BizIllegalException("订单已关闭");
        }
        // 5.旧单已经存在，判断支付渠道是否一致
        if (!StringUtils.equals(oldOrder.getPayChannelCode(), applyDTO.getPayChannelCode())) {
            // 支付渠道不一致，需要重置数据，然后重新申请支付单
            PayOrder payOrder = buildPayOrder(applyDTO);
            payOrder.setId(oldOrder.getId());
            payOrder.setQrCodeUrl("");
            updateById(payOrder);
            payOrder.setPayOrderNo(oldOrder.getPayOrderNo());
            return payOrder;
        }
        // 6.旧单已经存在，且可能是未支付或未提交，且支付渠道一致，直接返回旧数据
        return oldOrder;
    }

    private PayOrder buildPayOrder(PayApplyDTO payApplyDTO) {
        // 1.数据转换
        PayOrder payOrder = BeanUtils.toBean(payApplyDTO, PayOrder.class);
        // 2.初始化数据
        payOrder.setPayOverTime(LocalDateTime.now().plusMinutes(120L));
        payOrder.setStatus(PayStatus.WAIT_BUYER_PAY.getValue());
        payOrder.setBizUserId(UserContext.getUserId());
        return payOrder;
    }

    public PayOrder queryByBizOrderNo(Long bizOrderNo) {
        return lambdaQuery()
                .eq(PayOrder::getBizOrderNo, bizOrderNo)
                .one();
    }
}
