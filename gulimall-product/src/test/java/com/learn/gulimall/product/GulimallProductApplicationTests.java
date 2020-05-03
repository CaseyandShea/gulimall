package com.learn.gulimall.product;

import com.learn.gulimall.product.dao.AttrGroupDao;
import com.learn.gulimall.product.vo.SkuItemVo;
import com.learn.gulimall.product.vo.SpuItemAttrGroupVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    StringRedisTemplate redisTemplate;


    @Autowired
    AttrGroupDao attrGroupDao;

    @Test
    void contextLoads() {
        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        //保存
        stringStringValueOperations.set("hello","world_"+ UUID.randomUUID().toString());

        //查询
        String hello = stringStringValueOperations.get("hello");
        System.out.println(hello);


    }

    @Test
    void test(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(13l, 225l);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

}
