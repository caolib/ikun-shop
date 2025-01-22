package io.github.caolib.enums;

import lombok.Getter;

@Getter
public enum Code {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "未找到"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    ORDER_ALREADY_PAY(1001, "订单已支付"),
    PARAME_ERROR(1002, "参数错误"),
    ADDR_ALREADY_MAX(1003, "地址数量已达上限"),
    ADDR_NOT_BELONG_USER(1004, "地址不属于当前登录用户"),

    ;

    private final int code;
    private final String message;

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }
}