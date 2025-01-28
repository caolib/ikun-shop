package io.github.caolib;

import lombok.Getter;

@Getter
public enum OrderStatus {
    NON_PAYMENT(1, "未付款"),
    PAID(2, "已付款"),
    SENT(3, "已发货"),
    SUCCESS(4, "交易成功"),
    CLOSED(5, "已关闭"),


    ;
    private final int code;
    private final String message;

    OrderStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
