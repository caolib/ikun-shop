package io.github.caolib.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
public class RabbitConfig {

    /**
     * 使用jackson序列化消息为json格式
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    // 定义死信交换机
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dead-letter-exchange");
    }

    // 定义死信队列
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("dead-letter-queue", true);
    }

    // 将死信队列绑定到死信交换机
    @Bean
    public Binding deadLetterBinding(DirectExchange deadLetterExchange, Queue deadLetterQueue) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dead-letter-routing-key");
    }

    // 定义普通队列并设置死信交换机和路由键
    @Bean
    public Queue normalQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dead-letter-exchange"); // 死信交换机
        args.put("x-dead-letter-routing-key", "dead-letter-routing-key"); // 死信路由键
        return new Queue("normal-queue", true, false, false, args);
    }
}
