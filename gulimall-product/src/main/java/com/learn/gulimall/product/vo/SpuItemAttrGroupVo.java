package com.learn.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/28 11:51 上午
 **/
@Data
public class SpuItemAttrGroupVo {
    private  String groupName;

    private List<SpuBaseAttrVo> attrs;
}
