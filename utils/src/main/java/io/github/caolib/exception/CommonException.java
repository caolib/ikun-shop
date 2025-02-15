package io.github.caolib.exception;

import io.github.caolib.enums.Code;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
    private final int code;

    public CommonException(String message, int code) {
        super(message);
        this.code = code;
    }

    public CommonException(Code c) {
        super(c.getMessage());
        this.code = c.getCode();
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
