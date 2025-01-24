package io.github.caolib.handler;

import io.github.caolib.domain.R;
import io.github.caolib.exception.AlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class CartExceptionHandler {

    @ExceptionHandler(AlreadyExistException.class)
    public R<Void> handleDbException(AlreadyExistException e) {
        String message = e.getMessage();
        log.error(message);

        return R.error(e.getCode(), message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = ex.getMessage();
        log.error(error);
        return R.error("参数类型异常");
    }

}