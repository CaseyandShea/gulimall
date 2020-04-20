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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    @Override
    public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
        List<ProductAttrValueEntity> spu_id = this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>()
                .eq("spu_id", spuId));

        return spu_id;
    }

    @Transactional
    @Override
    public void updateProductAttr(Long spuId, List<ProductAttrValueEntity> attrValueEntities) {
        //删除就属性
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>()
                .eq("spu_id", spuId));

        List<ProductAttrValueEntity> objectList = attrValueEntities.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        //新增新属性

        this.saveBatch(objectList);
    }

}