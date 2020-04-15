package com.learn.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * packageName = com.learn.gulimall.product.config
 * author = Casey
 * Data = 2020/4/15 10:45 下午
 **/

@Configuration
@EnableTransactionManagement //开启使用事物（只有开启事物才能使用事物）
@MapperScan("com.learn.gulimall.product.dao")
public class MybatisConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //设置页面大于最后一页的操作 true 回到首页，
        paginationInterceptor.setOverflow(true);

        //设置最大单页限制数量 默认500条  -1不受限制
        paginationInterceptor.setLimit(1000);

        return paginationInterceptor;
    }
}

