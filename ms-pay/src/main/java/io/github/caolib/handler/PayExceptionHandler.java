package io.github.caolib.handler;

import io.github.caolib.domain.R;
import io.github.caolib.exception.BizIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class PayExceptionHandler {

    @ExceptionHandler(BizIllegalException.class)
    public Object handleRuntimeException(BizIllegalException e) {
        String msg = e.getMessage();
        log.error("业务异常:{}", msg);
        return R.error(e.getCode(),msg);
    }
}
