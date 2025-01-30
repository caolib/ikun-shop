package io.github.caolib.domain.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用户下单 表单
 */
@Data
@Builder
public class PayFormDTO {
    /**
     * 业务订单id不能为空
     */
    @NotNull(message = "业务订单id不能为空")
    private Long bizOrderNo;
    /**
     * 支付金额必须为正数
     */
    @Min(value = 0, message = "支付金额必须为正数")
    private Integer amount;
    /**
     * 支付渠道编码不能为空
     */
    @NotNull(message = "支付渠道编码不能为空")
    private String payChannelCode;
    /**
     * 支付方式不能为空
     */
    @NotNull(message = "支付方式不能为空")
    private Integer payType;
    /**
     * 订单中的商品信息不能为空
     */
    @NotNull(message = "订单中的商品信息不能为空")
    private String orderInfo;
}