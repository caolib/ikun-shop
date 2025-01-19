package io.github.caolib.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cart")
public class CartProperties {
    /**
     * 购物车中商品最大种类数
     */
    private Integer maxCommodityNum;
}
