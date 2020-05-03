package com.learn.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/28 9:10 下午
 **/

@ToString
@Data
public class AttrValueWithSkuIdVo {

    private String attrValue;
    private String skuIds;

}
