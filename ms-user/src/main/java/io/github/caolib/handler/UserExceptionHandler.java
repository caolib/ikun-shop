package io.github.caolib.handler;


import io.github.caolib.domain.R;
import io.github.caolib.exception.GitHubLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {
    /**
     * 处理GitHub登录异常
     */
    @ExceptionHandler(GitHubLoginException.class)
    public Object handleRuntimeException(GitHubLoginException e) {
        String msg = e.getMessage();
        log.error(msg);
        return R.error(msg);
    }

    /**
     * Github连接超时
     */
    @ExceptionHandler(ResourceAccessException.class)
    public Object handleRuntimeException(ResourceAccessException e) {
        String msg = e.getMessage();
        log.error(msg);
        if (msg != null && msg.contains("github.com/login/oauth/access_token")) {
            log.error("连接超时,请检查网络！");
        }
        return R.error(msg);
    }
}
