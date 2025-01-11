package io.github.caolib.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 交易下单表单实体
 */
@Data
public class OrderFormDTO {
    /**
     * 收货地址id
     */
    private Long addressId;
    /**
     * 支付类型
     */
    private Integer paymentType;
    /**
     * 下单商品列表
     */
    private List<OrderDetailDTO> details;
}
