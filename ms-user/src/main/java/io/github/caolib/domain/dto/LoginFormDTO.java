package io.github.caolib.domain.dto;

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
    @NotNull(message = "用户名不能为空")
    private String username;
    /**
     * 密码不能为空
     */
    @NotNull(message = "密码不能为空")
    private String password;
    /**
     * 是否记住我
     */
    private Boolean rememberMe = false;
}