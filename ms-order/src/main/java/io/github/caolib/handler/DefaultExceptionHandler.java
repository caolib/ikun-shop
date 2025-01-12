package io.github.caolib.handler;


import io.github.caolib.domain.R;
import io.github.caolib.exception.BizIllegalException;
import io.seata.core.exception.RmTransactionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {


    /**
     * 业务异常
     */
    @ExceptionHandler(BizIllegalException.class)
    public R<String> exceptionHandler(BizIllegalException ex) {
        String message = ex.getMessage();
        log.error("业务异常：{}", message);
        return R.error(message);
    }


    /**
     * seata事务异常
     */
    @ExceptionHandler(RmTransactionException.class)
    public R<String> exceptionHandler(RmTransactionException ex) {
        String message = ex.getMessage();
        log.error("事务执行失败：{}", message);
        return R.error(message);
    }
}
