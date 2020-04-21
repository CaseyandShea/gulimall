package com.learn.gulimall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.gulimall.ware.entity.WmsWareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 14:33:02
 */
@Mapper
public interface WmsWareSkuDao extends BaseMapper<WmsWareSkuEntity> {

    Long getSkuStock(@Param("skuId") Long skuId);
}
