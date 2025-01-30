package io.github.caolib.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVO implements Serializable {
    public String oauth_name; // 第三方账号用户名
    public String avatar_url; // 第三方账号头像
    private String username;
    private String phone;
    private Long balance;
    private String provider; // 绑定的第三方账号平台
}
