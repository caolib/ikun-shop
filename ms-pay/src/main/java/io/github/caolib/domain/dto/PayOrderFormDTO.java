package io.github.caolib.domain.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 支付确认表单实体
 */
@Data
@Builder
public class PayOrderFormDTO {
    /**
     * 支付订单id不能为空
     */
    @NotNull(message = "支付订单id不能为空")
    private Long id;
    /**
     * 支付密码
     */
    @NotNull(message = "支付密码")
    private String pw;
}