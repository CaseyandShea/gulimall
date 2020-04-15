package com.learn.gulimall.product.vo;

import lombok.Data;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/16 12:34 上午
 **/
@Data
public class AttrRespVo extends AttrVo {
    /**
     * 所属分类名称
     */
    private String catelogName;

    /**
     * 所属分组名字
     */
    private String groupName;


    /**
     * cateLog路径名
     */
    private Long[] catelogPath;
}
