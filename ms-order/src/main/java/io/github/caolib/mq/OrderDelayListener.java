package io.github.caolib.mq;

import io.github.caolib.client.PayClient;
import io.github.caolib.domain.vo.PayOrderVO;
import io.github.caolib.enums.Q;
import io.github.caolib.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDelayListener {

    private final PayClient payClient;
    private final IOrderService orderService;

    /**
     * 监听订单延迟消息，检查订单是否已支付
     *
     * @param orderId 订单id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = Q.PAY_DELAY_Q,durable = "true"),
            exchange = @Exchange(name = Q.PAY_DELAY_EXCHANGE,delayed = "true"),
            key = Q.PAY_DELAY_KEY))
    public void listenOrderPaySuccess(Long orderId) {
        // 查询订单对应的支付单
        PayOrderVO payOrder = payClient.getPayOrderByOrderId(orderId);

        // 已支付
        if (payOrder != null && payOrder.getStatus() == 3) {
            orderService.markOrderPaySuccess(orderId);
            log.debug("<MQ <-- 标记订单 {} 支付成功>", orderId);
            return;
        }

        // 未支付，订单超时，取消订单，释放库存
        log.info("<MQ <-- 订单 {} 超时取消>", orderId);
        orderService.markOrderTimeout(orderId);
    }

}
