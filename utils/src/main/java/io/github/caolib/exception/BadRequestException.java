package io.github.caolib.exception;

import io.github.caolib.enums.Code;

public class BadRequestException extends CommonException{

    public BadRequestException(Code code) {
        super(code.getMessage(), code.getCode());
    }

    public BadRequestException(String message) {
        super(message, 400);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause, 400);
    }

    public BadRequestException(Throwable cause) {
        super(cause, 400);
    }
}
