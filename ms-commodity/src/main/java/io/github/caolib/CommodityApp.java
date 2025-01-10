package io.github.caolib;

import io.github.caolib.config.FeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(defaultConfiguration = FeignConfig.class)
@MapperScan(basePackages = "io.github.caolib.mapper")
@SpringBootApplication
public class CommodityApp {
    public static void main(String[] args) {
        SpringApplication.run(CommodityApp.class, args);
    }
}