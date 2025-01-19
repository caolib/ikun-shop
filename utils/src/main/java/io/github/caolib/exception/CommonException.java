package io.github.caolib.exception;

import lombok.Getter;

// TODO 添加只有msg的构造方法
@Getter
public class CommonException extends RuntimeException{
    private final int code;

    public CommonException(String message, int code) {
        super(message);
        this.code = code;
    }

    public CommonException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public CommonException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }
}
