package com.learn.gulimall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * packageName = com.learn.gulimall.auth.config
 * author = Casey
 * Data = 2020/4/29 4:02 下午
 **/

@Configuration
public class gulimallWebConfig implements WebMvcConfigurer {
    /**
     * 视图映射
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/res.html").setViewName("res");
    }
}
