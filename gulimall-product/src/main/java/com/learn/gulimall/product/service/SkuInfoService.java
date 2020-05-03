package com.learn.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.product.entity.SkuInfoEntity;
import com.learn.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:01:06
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkuBySpuId(Long spuId);

    SkuItemVo getItem(Long skuId);
}

