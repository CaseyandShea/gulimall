package com.learn.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:01:06
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    void updateProductAttr(Long spuId, List<ProductAttrValueEntity> attrValueEntities);
}

