package io.github.caolib.mq;

import io.github.caolib.enums.Q;
import io.github.caolib.service.IPayOrderService;
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
public class PayOrderListener {

    private final IPayOrderService payOrderService;

    /**
     * 监听 删除支付单消息（逻辑删除）
     *
     * @param userId 用户id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = Q.PAY_ORDER_DELETE_Q, durable = "true"),
            exchange = @Exchange(name = Q.PAY_EXCHANGE),
            key = Q.PAY_DELETE_KEY
    ))
    public void listenOrderPaySuccess(Long userId) {
        // 逻辑删除用户所有支付单
        payOrderService.deleteByUserId(userId);
        log.debug("<MQ <-- 成功删除用户 {} 的支付单信息>", userId);
    }
}
