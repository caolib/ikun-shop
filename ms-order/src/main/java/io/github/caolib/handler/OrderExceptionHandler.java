package io.github.caolib.handler;


import io.github.caolib.domain.R;
import io.github.caolib.exception.BizIllegalException;
import io.seata.core.exception.RmTransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static io.github.caolib.utils.LogUtil.logErr;

@RestControllerAdvice
public class OrderExceptionHandler {


    /**
     * 业务异常
     */
    @ExceptionHandler(BizIllegalException.class)
    public R<String> exceptionHandler(BizIllegalException ex) {
        String message = ex.getMessage();
        logErr(ex, "业务异常" + message);
        return R.error(message);
    }


    /**
     * seata事务异常
     */
    @ExceptionHandler(RmTransactionException.class)
    public R<String> exceptionHandler(RmTransactionException ex) {
        String message = ex.getMessage();
        logErr(ex, "事务执行失败" + message);

        if (message.contains("Timeout"))
            return R.error("请求超时，请稍后再试");

        return R.error(message);
    }
}
