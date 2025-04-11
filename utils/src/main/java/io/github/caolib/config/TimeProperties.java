package io.github.caolib.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "time")
public class TimeProperties {
    private Long payTimeout; // 支付超时时间 ms
}
