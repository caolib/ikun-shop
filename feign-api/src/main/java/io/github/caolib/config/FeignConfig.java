package io.github.caolib.config;

import feign.Logger;
import feign.RequestInterceptor;
import io.github.caolib.enums.Auth;
import io.github.caolib.utils.UserContext;
import org.springframework.context.annotation.Bean;

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
            Long userId = UserContext.getUser();
            if (userId != null)
                requestTemplate.header(Auth.USER_ID, userId.toString());
        };
    }
}
