package io.github.caolib.service.impl;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.client.OrderClient;
import io.github.caolib.client.UserClient;
import io.github.caolib.config.TimeProperties;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.PayFormDTO;
import io.github.caolib.domain.dto.PayOrderFormDTO;
import io.github.caolib.domain.po.PayOrder;
import io.github.caolib.domain.vo.PayDetailResVO;
import io.github.caolib.domain.vo.PayDetailVO;
import io.github.caolib.domain.vo.PayOrderVO;
import io.github.caolib.domain.vo.PayStatisticVO;
import io.github.caolib.enums.Code;
import io.github.caolib.enums.E;
import io.github.caolib.enums.PayStatus;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements IPayOrderService {
    private final UserClient userClient;
    private final OrderClient orderClient;
    private final RabbitTemplate rabbitTemplate;
    private final PayOrderMapper payOrderMapper;
    private final TimeProperties timeProperties;

    /**
     * 查询用户支付单
     */
    @Override
    public List<PayOrderVO> getUserPayOrders(Long userId) {
        // 查询用户支付单
        List<PayOrder> list = lambdaQuery().eq(PayOrder::getBizUserId, userId).list();

        return BeanUtils.copyList(list, PayOrderVO.class);
    }

    /**
     * 根据业务订单id查询支付单id
     *
     * @param bizOrderId 业务订单id
     * @return 支付单id
     */
    @Override
    public PayOrderVO getPayOrderId(Long bizOrderId) {
        PayOrder payOrder = lambdaQuery().eq(PayOrder::getBizOrderNo, bizOrderId).one();
        return BeanUtils.copyBean(payOrder, PayOrderVO.class);
    }


    /**
     * 创建支付单
     *
     * @param payForm 用户下单表单
     */
    @Override
    public R<PayOrder> createPayOrder(PayFormDTO payForm) {
        // 幂等性校验
        PayOrder payOrder = checkIdempotent(payForm);
        // 返回支付单
        return R.ok(payOrder);
    }

    /**
     * 使用用户余额支付
     *
     * @param payOrderFormDTO 支付订单表单数据传输对象
     */
    @Override
    @GlobalTransactional
    public void payOrderByBalance(PayOrderFormDTO payOrderFormDTO) {
        //log.debug("开始支付...");
        String errorMsg = E.ORDER_STATUS_EXCEP;

        // 查询支付单
        PayOrder po = getById(payOrderFormDTO.getId());
        //log.debug("支付单查询结果：{}", po);
        // 判断状态
        if (!PayStatus.WAIT_BUYER_PAY.equalsValue(po.getStatus())) {
            throw new BizIllegalException(errorMsg);// 订单不是未支付
        }
        // RPC --> 扣减用户余额
        R<String> res = userClient.deductMoney(payOrderFormDTO.getPw(), po.getAmount(), UserContext.getUserId());
        //log.debug("扣减用户余额结果：{}", res);
        if (res.getCode() != 200) {
            throw new BizIllegalException(res.getMsg(), res.getCode());// 扣减余额失败
        }

        // 修改支付单状态
        boolean success = markPayOrderSuccess(payOrderFormDTO.getId(), LocalDateTime.now());
        if (!success) throw new BizIllegalException(errorMsg);


        // RPC --> 修改订单状态为支付成功
        Long orderId = po.getBizOrderNo();
        orderClient.markOrderPaySuccess(orderId);
    }


    /**
     * 根据用户id删除支付单
     *
     * @param userId 用户id
     */
    @Override
    public void deleteByUserId(Long userId) {
        payOrderMapper.updatePayStatusByUserId(userId);
    }


    @Override
    public void cancelPayOrder(Long orderId) {
        // 查询支付单
        PayOrder payOrder = lambdaQuery().eq(PayOrder::getBizOrderNo, orderId).one();

        if (payOrder == null) {
            throw new BizIllegalException(Code.PAY_ORDER_NOT_FOUND);
        }
        // 修改支付单状态
        payOrder.setStatus(PayStatus.TRADE_CLOSED.getValue());
        updateById(payOrder);
    }

    @Override
    public R<List<PayStatisticVO>> dayStatistic(int days) {
        // 获取当前时间和指定天数前的时间
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1).minusNanos(1);
        LocalDateTime startOfPeriod = endOfDay.minusDays(days - 1);

        // 创建一个列表来存储每一天的统计数据
        List<PayStatisticVO> statistics = new ArrayList<>();

        // 循环获取每一天的统计数据
        for (LocalDateTime date = startOfPeriod; !date.isAfter(endOfDay); date = date.plusDays(1)) {
            PayStatisticVO dailyStatistic = dayStatistic(date);
            statistics.add(dailyStatistic);
        }

        return R.ok(statistics);
    }

    /**
     * 查询一段时间内商品销售详情
     *
     * @param durationStart 开始日期
     * @param durationEnd   结束日期
     */
    @Override
    public R<List<PayDetailResVO>> payDetail(LocalDate durationStart, LocalDate durationEnd) {
        List<PayDetailResVO> result = new ArrayList<>();

        // 遍历每一天的日期
        for (LocalDate date = durationStart; !date.isAfter(durationEnd); date = date.plusDays(1)) {
            // 查询当天的支付单号
            List<Long> payOrderIds = lambdaQuery()
                    .between(PayOrder::getPaySuccessTime, date.atStartOfDay(), date.plusDays(1).atStartOfDay().minusNanos(1))
                    .list()
                    .stream()
                    .map(PayOrder::getBizOrderNo)
                    .toList();

            if (payOrderIds.isEmpty()) {
                result.add(new PayDetailResVO().setDate(date).setPayDetailList(new ArrayList<>()));
            }

            // 使用 orderClient 获取订单详情
            List<PayDetailVO> payDetail = orderClient.getPayDetail(payOrderIds);

            // 封装结果
            PayDetailResVO payDetailResVO = new PayDetailResVO()
                    .setDate(date)
                    .setPayDetailList(payDetail);
            result.add(payDetailResVO);
        }

        return R.ok(result);
    }

    /**
     * 查询某天的支付统计
     *
     * @param time 时间
     */
    public PayStatisticVO dayStatistic(LocalDateTime time) {
        // 获取当天的开始时间和结束时间
        LocalDateTime startOfDay = time.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        // 查询当天的所有支付单
        List<PayOrder> list = lambdaQuery().between(PayOrder::getPaySuccessTime, startOfDay, endOfDay).list();
        Long totalFee = 0L;
        Long totalOrder = (long) list.size();

        // 计算总金额
        for (PayOrder payOrder : list)
            totalFee += payOrder.getAmount();

        return PayStatisticVO.builder()
                .totalFee(totalFee)
                .totalOrder(totalOrder)
                .time(time)
                .build();
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
                .in(PayOrder::getStatus, PayStatus.NOT_COMMIT.getValue(), PayStatus.WAIT_BUYER_PAY.getValue())
                .update();
    }


    private PayOrder checkIdempotent(PayFormDTO applyDTO) {
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
    private PayOrder buildPayOrder(PayFormDTO payApplyDTO) {
        // 转换为PO
        PayOrder payOrder = BeanUtils.toBean(payApplyDTO, PayOrder.class);
        // 初始化数据
        payOrder.setPayOverTime(LocalDateTime.now().plusSeconds(timeProperties.getPayTimeout() / 1000)); // 设置支付单超时时间
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
