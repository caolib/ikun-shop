package io.github.caolib.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
