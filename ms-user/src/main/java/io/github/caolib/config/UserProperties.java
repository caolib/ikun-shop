package io.github.caolib.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "user")
public class UserProperties {
    private Integer initBalance; // 用户初始余额
    private String initPassword; // 用户初始密码
}
