package io.github.caolib.domain.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private String username;
    private String phone;
    private Long balance;
    private String provider; // 绑定的第三方账号平台
    public String oauth_name; // 第三方账号用户名
    public String avatar_url; // 第三方账号头像
}
