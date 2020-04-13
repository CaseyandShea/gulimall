package com.learn.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.product.entity.AttrAttrgroupRelationEntity;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:01:06
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

