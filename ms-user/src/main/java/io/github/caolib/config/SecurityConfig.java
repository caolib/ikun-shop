package io.github.caolib.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, AuthProperties.class,AddrProperties.class})
public class SecurityConfig {

    /**
     * 密码加密方式
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public KeyPair keyPair(JwtProperties key) {
        Resource location = key.getLocation();
        char[] charArray = key.getPassword().toCharArray();
        // 获取秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(location, charArray);
        //读取钥匙对
        return keyStoreKeyFactory.getKeyPair(key.getAlias(), charArray);
    }
}