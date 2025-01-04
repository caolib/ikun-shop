package io.github.caolib;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan(basePackages = "io.github.caolib.mapper")
@SpringBootApplication
public class CommodityApp {
    public static void main(String[] args) {
        SpringApplication.run(CommodityApp.class, args);
    }
}