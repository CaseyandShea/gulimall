package com.learn.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:01:06
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    /**
     * 自定义批量删除菜单功能
     * @param catIds
     */
    void removeMenuByIds(List<Long> catIds);
}

