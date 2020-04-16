package com.learn.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.gulimall.product.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 商品三级分类
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:01:06
 */
@Mapper
@Component
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
