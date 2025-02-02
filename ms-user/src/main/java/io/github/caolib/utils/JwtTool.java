package io.github.caolib.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import io.github.caolib.config.JwtProperties;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.po.AdminUser;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.enums.Auth;
import io.github.caolib.enums.Code;
import io.github.caolib.enums.UserStatus;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.exception.ForbiddenException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTool {
    private final JWTSigner jwtSigner;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;


    public JwtTool(KeyPair keyPair, JwtProperties jwtProperties, PasswordEncoder passwordEncoder) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 创建jwt token
     *
     * @param userId   用户id
     * @param identity 用户身份
     * @param ttl      token有效期
     */
    public String createToken(Long userId, String identity, Duration ttl) {
        return JWT.create()
                .setPayload(Auth.USER_ID, userId)
                .setPayload(Auth.USER_IDENTITY, identity)
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
    public UserLoginVO setReturnUser(User user, String identity, String avatarUrl) {
        String token = createToken(user.getId(), identity, jwtProperties.getTokenTTL());

        return UserLoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .balance(user.getBalance())
                .token(token)
                .avatar(avatarUrl)
                .build();
    }

    /**
     * 设置登录返回的用户信息
     *
     * @param admin     管理员
     * @param avatarUrl 头像
     */
    public UserLoginVO setReturnUser(AdminUser admin, String identity, String avatarUrl) {
        String token = createToken(admin.getId(), identity, jwtProperties.getTokenTTL());

        return UserLoginVO.builder()
                .userId(admin.getId())
                .username(admin.getUsername())
                .token(token)
                .avatar(avatarUrl)
                .build();
    }

    /**
     * 校验用户信息
     */
    public  void checkUser(LoginFormDTO loginDTO, String identity, UserStatus status, String pwd) {
        // 校验用户身份
        if (!loginDTO.getIdentity().equals(identity)) throw new BadRequestException(Code.IDENTITY_ERROR);
        // 校验账号状态
        if (status == UserStatus.FROZEN) throw new ForbiddenException(Code.USER_IS_FROZEN);
        // 校验密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), pwd))
            throw new BadRequestException(Code.USERNAME_OR_PASSWORD_ERROR);
    }

}