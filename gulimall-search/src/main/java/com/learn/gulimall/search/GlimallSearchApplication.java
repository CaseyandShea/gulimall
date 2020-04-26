package com.learn.gulimall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * packageName = com.learn.gulimall.search
 * author = Casey
 * Data = 2020/4/20 1:40 下午
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableSwagger2
public class GlimallSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(GlimallSearchApplication.class, args);
    }

}
