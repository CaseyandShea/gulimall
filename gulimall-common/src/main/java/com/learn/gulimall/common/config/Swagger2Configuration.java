package com.learn.gulimall.common.config;

import com.google.common.base.Predicates;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * packageName = com.learn.gulimall.common.config
 * author = Casey
 * Data = 2020/4/14 4:51 下午
 **/


@Configuration
@EnableSwagger2
public class Swagger2Configuration {
    public static final String VERSION = "1.0.0";
    @Bean
    public Docket webApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("商城Api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.learn.gulimall"))
                .paths(Predicates.not(PathSelectors.regex("/admin/.*"))) //接口中有admin 和 error时就不显示
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();


    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("开心商城接口文档") //设置文档的标题
                .description("包括产品管理、订单管理、优惠管理等 API 接口文档") // 设置文档的描述
                .version(VERSION) // 设置文档的版本信息-> 1.0.0 Version information
                .termsOfServiceUrl("http://www.baidu.com") // 设置文档的License信息->1.3 License information
                .build();
    }

}
