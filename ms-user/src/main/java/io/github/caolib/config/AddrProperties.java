package io.github.caolib.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "addr")
public class AddrProperties {
    private Integer maxCount; // 单个用户最大地址数量
}
