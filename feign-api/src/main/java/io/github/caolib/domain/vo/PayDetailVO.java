package io.github.caolib.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PayDetailVO implements Serializable {
    // 商品id
    private Long itemId;
    // 商品名称
    private String name;
    // 商品销售数量
    private Integer num;
}

