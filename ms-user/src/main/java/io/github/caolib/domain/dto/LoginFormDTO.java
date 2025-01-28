package io.github.caolib.domain.dto;

import io.github.caolib.enums.E;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 登录表单实体
 */
@Data
public class LoginFormDTO {
    /**
     * 用户名不能为空
     */
    @NotNull(message = E.USERNAME_IS_NULL)
    private String username;
    /**
     * 密码不能为空
     */
    @NotNull(message = E.PWD_IS_NULL)
    private String password;
    /**
     * TODO 是否记住我 待删除
     */
    private Boolean rememberMe = false;
}