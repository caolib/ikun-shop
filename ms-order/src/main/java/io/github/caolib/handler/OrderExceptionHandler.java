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
        logErr(ex, message);
        return R.error(message);
    }


    /**
     * seata事务异常
     */
    @ExceptionHandler(RmTransactionException.class)
    public R<String> exceptionHandler(RmTransactionException ex) {
        String msg = ex.getMessage();
        if (msg.contains("Timeout")) {
            logErr(ex, "事务执行失败 " + msg);
            return R.error("请求超时，请稍后再试");
        }
        logErr(ex, "事务执行失败 " + msg);

        return R.error(msg);
    }
}
