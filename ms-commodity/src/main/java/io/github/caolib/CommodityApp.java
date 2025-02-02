package io.github.caolib;

import io.github.caolib.config.FeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@EnableCaching
@EnableFeignClients(defaultConfiguration = FeignConfig.class)
@MapperScan(basePackages = "io.github.caolib.mapper")
@SpringBootApplication
public class CommodityApp {
    public static void main(String[] args) {
        SpringApplication.run(CommodityApp.class, args);
    }
}