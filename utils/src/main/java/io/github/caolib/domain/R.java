package io.github.caolib.domain;

import io.github.caolib.enums.Code;
import io.github.caolib.exception.CommonException;
import lombok.Data;

import java.io.Serializable;


@Data
public class R<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public R() {
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R<Void> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, "成功", data);
    }

    public static <T> R<T> error(String msg) {
        return new R<>(500, msg, null);
    }

    public static <T> R<T> error(Code code) {
        return new R<>(code.getCode(), code.getMessage(), null);
    }

    public static <T> R<T> error(int code, String msg) {
        return new R<>(code, msg, null);
    }

    public static <T> R<T> error(CommonException e) {
        return new R<>(e.getCode(), e.getMessage(), null);
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
