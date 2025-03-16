package io.github.caolib.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import io.github.caolib.domain.dto.UserInfo;
import io.github.caolib.enums.Auth;
import io.github.caolib.enums.Code;
import io.github.caolib.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Date;

import static io.github.caolib.utils.LogUtil.logErr;

@Slf4j
@Component
public class JwtTool {
    private final JWTSigner jwtSigner;
    private final StringRedisTemplate redisTemplate;

    public JwtTool(KeyPair keyPair, StringRedisTemplate redisTemplate) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
        this.redisTemplate = redisTemplate;
    }

    /**
     * 解析token
     *
     * @param token token
     */
    public UserInfo parseToken(String token) {
        // 判断token是否为空
        if (token == null) throw new UnauthorizedException("未登录");

        JWT jwt;
        // 解析token
        try {
            //log.debug("token:{}", token);
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            logErr(e, "token解析失败");
            throw new UnauthorizedException("无效的token", e);
        }

        // 验证token有效性
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
            throw new UnauthorizedException(Code.TOKEN_EXPIRED);
        }
        // 获取token中的用户信息
        Object userPayload = jwt.getPayload(Auth.USER_ID);
        Object userIdentity = jwt.getPayload(Auth.USER_IDENTITY);
        if (userPayload == null || userIdentity == null) {
            log.error("token数据载荷为空");
            throw new UnauthorizedException("无效的token");
        }
        // 尝试解析数据并返回
        try {
            Long userId = Long.valueOf(userPayload.toString());
            String identity = userIdentity.toString();
            // 如果redis中没有token，说明token已经过期
            log.debug("userId:{}", userId);
            String storeToken = redisTemplate.opsForValue().get(userId.toString());
            if (storeToken == null) throw new UnauthorizedException(Code.TOKEN_EXPIRED); // token已经过期
            // 返回用户信息
            return new UserInfo(userId, identity);
        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            logErr(e, "redis连接失败");
            throw new UnauthorizedException("数据库服务繁忙，请稍后再试");
        } catch (UnauthorizedException e) {
            throw e;
        } catch (RuntimeException e) {
            logErr(e, "token数据载荷有误");
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