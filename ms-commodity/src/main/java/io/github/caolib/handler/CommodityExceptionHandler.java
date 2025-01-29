package io.github.caolib.handler;

import io.github.caolib.domain.R;
import io.github.caolib.exception.BizIllegalException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static io.github.caolib.utils.LogUtil.logErr;

@RestControllerAdvice
public class CommodityExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BizIllegalException.class)
    public R<Void> exceptionHandler(BizIllegalException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        return R.error(msg);
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(BindException.class)
    public R<Map<String, String>> handleBindException(BindException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        logErr(e, errors.toString());
        return R.error("参数异常");
    }

}
