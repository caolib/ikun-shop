package io.github.caolib.config;

import feign.Logger;
import feign.RequestInterceptor;
import io.github.caolib.enums.Auth;
import io.github.caolib.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
public class FeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // openfeign 日志级别
    }

    /**
     * feign 请求拦截器，传递用户信息
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Long userId = UserContext.getUserId();
            if (userId != null)
                requestTemplate.header(Auth.USER_ID, userId.toString());
            else
                log.error("feign请求拦截器:用户ID为空");
        };
    }
}
