package com.learn.gulimall.product.vo;

import lombok.Data;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/20 7:18 下午
 **/
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;
}
