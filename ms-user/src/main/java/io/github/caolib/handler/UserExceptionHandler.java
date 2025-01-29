package io.github.caolib.handler;


import io.github.caolib.domain.R;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.exception.GitHubLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import static io.github.caolib.utils.LogUtil.logErr;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {
    /**
     * 处理GitHub登录异常
     */
    @ExceptionHandler(GitHubLoginException.class)
    public Object handleRuntimeException(GitHubLoginException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        return R.error(msg);
    }

    /**
     * Github连接超时
     */
    @ExceptionHandler(ResourceAccessException.class)
    public Object handleRuntimeException(ResourceAccessException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        if (msg != null && msg.contains("github.com/login/oauth/access_token")) {
            log.error("连接超时,请检查网络！");
        }
        return R.error(msg);
    }

    /**
     * 请求异常
     */
    @ExceptionHandler(BadRequestException.class)
    public Object handleRuntimeException(BadRequestException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        return R.error(msg);
    }

    /**
     * 参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleRuntimeException(IllegalArgumentException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        return R.error(msg);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BizIllegalException.class)
    public Object handleRuntimeException(BizIllegalException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        return R.error(msg);
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage();
        logErr(e, msg);
        return R.error(msg);
    }
}
