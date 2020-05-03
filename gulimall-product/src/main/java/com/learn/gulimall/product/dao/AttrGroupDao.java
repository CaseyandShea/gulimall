package com.learn.gulimall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.gulimall.product.entity.AttrGroupEntity;
import com.learn.gulimall.product.vo.SkuItemVo;
import com.learn.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 属性分组
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:01:06
 */
@Mapper
@Component
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catelogId") Long catelogId);
}
