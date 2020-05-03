package com.learn.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/28 10:54 上午
 **/
@ToString
@Data
public class ItemSaleAttrVo {

    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;

    private List<AttrValueWithSkuIdVo> attrValues;
}
