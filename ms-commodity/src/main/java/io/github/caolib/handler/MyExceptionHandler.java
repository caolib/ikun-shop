package io.github.caolib.handler;

import io.github.caolib.domain.R;
import io.github.caolib.exception.BizIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(BizIllegalException.class)
    public R<Void> exceptionHandler(BizIllegalException ex) {
        String message = ex.getMessage();
        log.error("业务异常:{}", message);
        return  R.error(message);
    }
}
