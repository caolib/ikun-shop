package io.github.caolib.exception;

import io.github.caolib.enums.Code;

public class UnauthorizedException extends CommonException {

    public UnauthorizedException(String message) {
        super(message, 401);
    }

    public UnauthorizedException(Code code) {
        super(code.getMessage(), code.getCode());
    }

    public UnauthorizedException(String message, int code) {
        super(message, code);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, 401);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause, 401);
    }
}
