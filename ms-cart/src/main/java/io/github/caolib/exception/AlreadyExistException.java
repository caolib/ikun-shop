package io.github.caolib.exception;

public class AlreadyExistException extends CommonException{
    public AlreadyExistException(String message, int code) {
        super(message, code);
    }

    public AlreadyExistException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }

    public AlreadyExistException(Throwable cause, int code) {
        super(cause, code);
    }
}
