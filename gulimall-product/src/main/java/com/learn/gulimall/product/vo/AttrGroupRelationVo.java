package com.learn.gulimall.product.vo;

import lombok.Data;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/16 11:03 上午
 **/

@Data
public class AttrGroupRelationVo {
    /**
     * attrId 属性ID
     */
    private Long attrId;

    /**
     * attrGroupId 属性分组Id
     */
    private Long attrGroupId;
}
