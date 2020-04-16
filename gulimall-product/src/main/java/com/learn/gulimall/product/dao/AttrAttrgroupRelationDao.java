package com.learn.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.gulimall.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:01:06
 */
@Mapper
@Component
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelation(@Param("relationEntities") List<AttrAttrgroupRelationEntity> relationEntities);
}
