package io.github.caolib.enums;

import lombok.Getter;

@Getter
public enum Code {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    USER_IS_FROZEN(403, "账号已冻结"),
    NOT_FOUND(404, "未找到"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    ORDER_ALREADY_PAY(1001, "订单已支付"),
    PARAME_ERROR(1002, "参数错误"),
    ADDR_ALREADY_MAX(1003, "地址数量已达上限"),
    ADDR_NOT_BELONG_USER(1004, "地址不属于当前登录用户"),
    USER_NOT_EXIST(1005, "用户不存在"),
    INSUFFICIENT_BALANCE(1006, "余额不足"),
    USERNAME_OR_PASSWORD_ERROR(1007, "密码错误"),
    ORDER_IS_CLOSED(1008, "订单已关闭"),
    USERNAME_ALREADY_EXIST(1009, "用户名已存在"),
    PHONE_ALREADY_EXIST(1010, "电话已存在"),
    PAY_ORDER_NOT_FOUND(1011, "支付单未找到"),

    ;
    private final int code;
    private final String message;

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }
}