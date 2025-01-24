package io.github.caolib.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import io.github.caolib.config.JwtProperties;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.vo.UserLoginVO;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTool {
    private final JWTSigner jwtSigner;
    private final JwtProperties jwtProperties;


    public JwtTool(KeyPair keyPair, JwtProperties jwtProperties) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
        this.jwtProperties = jwtProperties;
    }

    /**
     * 创建 access-token
     *
     * @param userId 用户ID
     * @return access-token
     */
    public String createToken(Long userId, Duration ttl) {
        // 1.生成jws
        return JWT.create()
                .setPayload("user", userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * 设置登录返回的用户信息
     *
     * @param user      用户
     * @param avatarUrl 头像
     */
    public UserLoginVO setReturnUser(User user, String avatarUrl) {
        String token = createToken(user.getId(), jwtProperties.getTokenTTL());
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setBalance(user.getBalance());
        vo.setToken(token);
        vo.setAvatar(avatarUrl);

        return vo;
    }

}