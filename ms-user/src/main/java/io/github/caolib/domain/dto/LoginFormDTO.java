package io.github.caolib.domain.dto;

import io.github.caolib.enums.E;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 登录表单实体
 */
@Data
public class LoginFormDTO implements Serializable {
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
     * 用户身份
     */
    @NotNull(message = E.IDENTITY_IS_NULL)
    private String identity;

}