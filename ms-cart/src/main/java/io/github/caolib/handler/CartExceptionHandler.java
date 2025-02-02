package io.github.caolib.handler;

import io.github.caolib.domain.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static io.github.caolib.utils.LogUtil.logErr;

@RestControllerAdvice
public class CartExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = ex.getMessage();
        logErr(ex, "参数类型异常" + error);
        return R.error("参数类型异常");
    }

}