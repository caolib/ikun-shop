package io.github.caolib.exception;

import io.github.caolib.enums.Code;

public class ForbiddenException extends CommonException {

    public ForbiddenException(Code code) {
        super(code.getMessage(), code.getCode());
    }

    public ForbiddenException(String message) {
        super(message, 403);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, 403);
    }

    public ForbiddenException(Throwable cause) {
        super(cause, 403);
    }
}
