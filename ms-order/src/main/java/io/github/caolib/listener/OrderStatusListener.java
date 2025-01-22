package io.github.caolib.listener;

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
     * 监听订单支付成功消息，修改订单状态
     *
     * @param orderId 订单id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = Q.PAY_SUCCESS_Q, durable = "true"),
            exchange = @Exchange(name = Q.PAY_EXCHANGE),
            key = Q.PAY_SUCCESS_KEY
    ))
    public void listenOrderPaySuccess(Long orderId) {
        log.debug("监听到一条修改订单状态消息，正在修改订单 {} 的状态为支付成功", orderId);
        orderService.markOrderPaySuccess(orderId);
    }

}
