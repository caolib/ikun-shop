package io.github.caolib.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import io.github.caolib.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Date;

import static io.github.caolib.utils.LogUtil.logErr;

@Slf4j
@Component
public class JwtTool {
    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    public Long parseToken(String token) {
        if (token == null) {;
            throw new UnauthorizedException("未登录");
        }

        JWT jwt;
        // 解析token
        try {
            //log.debug("token:{}", token);
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            log.error("token解析失败");
            throw new UnauthorizedException("无效的token", e);
        }

        // 验证token
        if (!jwt.verify()) {
            log.error("token无效");
            throw new UnauthorizedException("无效的token");
        }

        // 验证token是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            logErr(e, "token已经过期");
            logDate(jwt); // 打印token过期时间
            throw new UnauthorizedException("token已经过期", 499);
        }


        // 获取token中的用户id
        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            log.error("token数载荷为空");
            throw new UnauthorizedException("无效的token");
        }

        // 解析用户id并返回
        try {
            return Long.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            log.error("token数据载荷有误");
            throw new UnauthorizedException("无效的token");
        }
    }

    /**
     * 打印token过期时间
     */
    public void logDate(JWT jwt) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer exp = (Integer) jwt.getPayload("exp");
        Date expiration = new Date(exp.longValue() * 1000);
        log.warn("token过期时间: {}", DATE_FORMAT.format(expiration));
    }
}