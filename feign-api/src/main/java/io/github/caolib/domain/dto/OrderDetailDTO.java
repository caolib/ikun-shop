package io.github.caolib.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderDetailDTO {
    // 商品id
    private Long itemId;
    // 商品购买数量
    private Integer num;
}
