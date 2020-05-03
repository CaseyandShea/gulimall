package com.learn.gulimall.product.vo;

import com.learn.gulimall.common.to.SpuBoundTo;
import com.learn.gulimall.product.entity.SkuImagesEntity;
import com.learn.gulimall.product.entity.SkuInfoEntity;
import com.learn.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/28 10:48 上午
 **/

@Data
public class SkuItemVo {
    //
    SkuInfoEntity info;

    boolean hasStock = true;

    //sku的图片
    List<SkuImagesEntity> images;

    //spu的销售属性组合
    List<ItemSaleAttrVo> saleAttrVos;

    //spu的介绍
    SpuInfoDescEntity desp;

    //Spu的规格参数信息
    List<SpuItemAttrGroupVo> groupVos;

}
