package com.learn.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * packageName = com.learn.gulimall.common.config
 * author = Casey
 * Data = 2020/4/22 7:05 下午
 **/

@Configuration
public class MyRedissonConfig {
    /**
     * @return
     * @throws IOException
     * @Bean 在容器中放一个RedissonClient的对象
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        //创建配置
        Config config = new Config();
        /*
        //集群模式
        config.useClusterServers()
                .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");*/
        //


        config.useSingleServer().setAddress("redis://192.168.56.9:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
