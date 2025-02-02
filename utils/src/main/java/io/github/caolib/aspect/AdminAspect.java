package io.github.caolib.aspect;

import io.github.caolib.enums.Auth;
import io.github.caolib.enums.E;
import io.github.caolib.exception.ForbiddenException;
import io.github.caolib.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AdminAspect {

    /**
     * 定义切点，匹配特定的类方法
     */
    @Pointcut("execution(* io.github.caolib.controller.admin..*(..))")
    public void adminMethods() {}

    /**
     * 管理员包下方法执行前校验身份
     */
    @Before("adminMethods()")
    public void beforeAdminMethods() {
        String identity = UserContext.getIdentity();

        // 管理员权限校验
        if (!Auth.ADMIN.equals(identity)) {
            log.debug("用户身份：{}", identity);
            throw new ForbiddenException(E.PERMISSIONDENIED);
        }
    }
}