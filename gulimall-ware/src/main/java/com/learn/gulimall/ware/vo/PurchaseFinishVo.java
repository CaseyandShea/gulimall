package com.learn.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * packageName = com.learn.gulimall.ware.vo
 * author = Casey
 * Data = 2020/4/17 4:08 下午
 **/
@Data
public class PurchaseFinishVo {
    @NotBlank
    private Long id;

    /**
     * 采购项
     */
    List<PurchaseItemDoneVo> items;

}
