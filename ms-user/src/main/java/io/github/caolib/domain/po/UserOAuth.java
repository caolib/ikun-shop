package io.github.caolib.domain.po;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@TableName("user_oauth")
public class UserOAuth implements Serializable {
    @TableId
    private Long id;

    private Long userId;
    private String provider;
    private String oauthId;
    private String username;
    private String accessToken;
    private String avatarUrl;
    private LocalDate createTime;
    private String email;
}
