package io.github.caolib.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class PayDetailVO implements Serializable {
    // 商品id
    private Long itemId;
    // 商品名称
    private String name;
    // 商品销售数量
    private Integer num;
}
