package com.learn.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.product.dao.ProductAttrValueDao;
import com.learn.gulimall.product.entity.ProductAttrValueEntity;
import com.learn.gulimall.product.service.ProductAttrValueService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

}