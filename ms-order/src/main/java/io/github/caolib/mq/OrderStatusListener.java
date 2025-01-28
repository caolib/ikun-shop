package io.github.caolib.mq;

import io.github.caolib.domain.po.Order;
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
public class OrderStatusListener {

    private final IOrderService orderService;

    /**
     * 监听订单支付成功消息，修改订单为成功状态
     *
     * @param orderId 订单id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = Q.PAY_SUCCESS_Q, durable = "true"),
            exchange = @Exchange(name = Q.PAY_EXCHANGE),
            key = Q.PAY_SUCCESS_KEY))
    public void listenOrderPaySuccess(Long orderId) {
        log.debug("<MQ <-- 修改订单 {} 支付成功>", orderId);

        Order order = orderService.getById(orderId);
        if (order == null || order.getStatus() != 1) {
            log.warn("<MQ <-- 订单 {} 不是待付款状态，修改失败>", orderId);
            return;
        }

        orderService.markOrderPaySuccess(orderId);
    }

}
