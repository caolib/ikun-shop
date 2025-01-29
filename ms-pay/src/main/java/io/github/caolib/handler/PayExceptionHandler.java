package io.github.caolib.handler;

import io.github.caolib.domain.R;
import io.github.caolib.exception.BizIllegalException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static io.github.caolib.utils.LogUtil.logErr;

@RestControllerAdvice
public class PayExceptionHandler {

    @ExceptionHandler(BizIllegalException.class)
    public Object handleRuntimeException(BizIllegalException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        return R.error(e.getCode(), msg);
    }
}
