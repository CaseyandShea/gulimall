package com.learn.gulimall.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * packageName = com.learn.gulimall.common.to.es
 * author = Casey
 * Data = 2020/4/20 5:26 下午
 **/

@Data
public class SkuEsModel {
    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;


    private Long catelogId;
    private String catelogName;

    private Long brandId;
    private String brandName;
    private String brandImg;

    private List<AttrModel> attrs;
}
