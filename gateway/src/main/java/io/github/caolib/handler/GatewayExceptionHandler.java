package io.github.caolib.handler;


import io.github.caolib.domain.R;
import io.github.caolib.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static io.github.caolib.utils.LogUtil.logErr;

@RestControllerAdvice
public class GatewayExceptionHandler {

    /**
     * 处理认证异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public R<Void> exceptionHandler(UnauthorizedException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        return R.error(msg);
    }
}
