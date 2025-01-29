package io.github.caolib.handler;

import io.github.caolib.domain.R;
import io.github.caolib.exception.CommonException;
import io.github.caolib.exception.DbException;
import io.github.caolib.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static io.github.caolib.utils.LogUtil.logErr;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DbException.class)
    public Object handleDbException(DbException e) {
        //log.error("mysql数据库操作异常 -> ", e);
        logErr(e, e.getMessage());
        return processResponse(e);
    }

    @ExceptionHandler(CommonException.class)
    public Object handleBadRequestException(CommonException e) {
        logErr(e, e.getMessage());
        return processResponse(e);
    }

    //@ExceptionHandler(MethodArgumentNotValidException.class)
    //public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    //    String msg = e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("|"));
    //    //log.error("请求参数校验异常 -> {}", msg);
    //    //log.debug("", e);
    //    logErr(e, msg);
    //    return processResponse(new BadRequestException(msg));
    //}

    //@ExceptionHandler(BindException.class)
    //public Object handleBindException(BindException e) {
    //    log.error("请求参数绑定异常 -> BindException， {}", e.getMessage());
    //    log.debug("", e);
    //    return processResponse(new BadRequestException("请求参数格式错误"));
    //}

    //@ExceptionHandler(NestedServletException.class)
    //public Object handleNestedServletException(NestedServletException e) {
    //    log.error("参数异常 -> NestedServletException，{}", e.getMessage());
    //    log.debug("", e);
    //    return processResponse(new BadRequestException("请求参数处理异常"));
    //}

    /**
     * 处理未授权异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public R<Void> handleUnAuthException(UnauthorizedException e) {
        //log.error("认证异常 -> ，{}", e.getMessage());
        //log.debug("", e);
        logErr(e, "认证异常 " + e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 表单校验异常
     */
    //@ExceptionHandler(ConstraintViolationException.class)
    //public R<String> handleConstraintViolationException(ConstraintViolationException ex) {
    //    return R.error(ex.getMessage());
    //}
    @ExceptionHandler(Exception.class)
    public Object handleRuntimeException(Exception e) {
        log.error("未知异常 : {}", e.getMessage(), e);
        return processResponse(new CommonException("服务器内部异常", 500));
    }


    /**
     * 过程响应
     *
     * @param e 异常
     * @return 响应实体
     */
    private ResponseEntity<R<Void>> processResponse(CommonException e) {
        return ResponseEntity.status(e.getCode()).body(R.error(e));
    }
}
