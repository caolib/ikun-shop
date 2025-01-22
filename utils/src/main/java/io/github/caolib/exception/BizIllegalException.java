package io.github.caolib.exception;

import io.github.caolib.enums.Code;

public class BizIllegalException extends CommonException {

    public BizIllegalException(String message) {
        super(message, 500);
    }

    public BizIllegalException(Code code) {
        super(code.getMessage(), code.getCode());
    }

    public BizIllegalException(String message, int code) {
        super(message, code);
    }

    public BizIllegalException(String message, Throwable cause) {
        super(message, cause, 500);
    }

    public BizIllegalException(Throwable cause) {
        super(cause, 500);
    }
}
