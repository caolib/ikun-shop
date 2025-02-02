package io.github.caolib.aspect;


import io.github.caolib.enums.Auth;
import io.github.caolib.enums.E;
import io.github.caolib.exception.ForbiddenException;
import io.github.caolib.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AdminAspect {

    /**
     * 管理员包下方法执行前校验身份
     */
    @Before("execution(* io.github.caolib.admin..*(..))")
    public void beforeAdminMethods() {
        String identity = UserContext.getIdentity();
        //log.debug("用户身份：{}", identity);

        // 管理员权限校验
        if (!Auth.ADMIN.equals(identity)) {
            throw new ForbiddenException(E.PERMISSIONDENIED);
        }
    }
}
