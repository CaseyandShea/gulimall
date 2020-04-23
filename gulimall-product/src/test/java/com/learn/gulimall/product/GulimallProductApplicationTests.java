package com.learn.gulimall.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        //保存
        stringStringValueOperations.set("hello","world_"+ UUID.randomUUID().toString());

        //查询
        String hello = stringStringValueOperations.get("hello");
        System.out.println(hello);


    }

}
