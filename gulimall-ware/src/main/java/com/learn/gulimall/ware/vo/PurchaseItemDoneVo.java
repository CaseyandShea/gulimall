package com.learn.gulimall.ware.vo;

import lombok.Data;

/**
 * packageName = com.learn.gulimall.ware.vo
 * author = Casey
 * Data = 2020/4/17 4:09 下午
 **/

@Data
public class PurchaseItemDoneVo {
    /**
     * detialId
     */
    private Long itemId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 原因
     */
    private String reason;

}
