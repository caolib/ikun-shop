package io.github.caolib.interceptor;

import cn.hutool.core.util.StrUtil;
import io.github.caolib.enums.Auth;
import io.github.caolib.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取用户id
        String userId = request.getHeader(Auth.USER_ID);
        if (StrUtil.isNotBlank(userId))
            UserContext.setUserId(Long.valueOf(userId)); // 保存到ThreadLocal中
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        UserContext.removeUser(); // 请求结束后清除ThreadLocal中的用户id
    }
}
