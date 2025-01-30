package io.github.caolib.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginVO implements Serializable {
    private String token;
    private Long userId;
    private String username;
    private Integer balance;
    private String avatar;
}
