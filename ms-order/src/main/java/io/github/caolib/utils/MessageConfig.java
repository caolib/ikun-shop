package io.github.caolib.utils;

import org.springframework.amqp.core.MessagePostProcessor;

public class MessageConfig {

   public static MessagePostProcessor getMessagePostProcessor(int delayTime) {
        return message -> {
            message.getMessageProperties().setDelay(delayTime);
            return message;
        };
    }
}
