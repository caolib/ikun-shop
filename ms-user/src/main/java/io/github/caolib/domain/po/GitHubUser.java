package io.github.caolib.domain.po;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitHubUser {

    private Long id;
    private String login; // 用户名
    private String name;  // 昵称
    @JsonProperty("avatar_url")
    private String avatarUrl; // 头像 URL
    private String email;  // 邮箱
}
