package com.learn.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * packageName = com.learn.gulimall.ware.vo
 * author = Casey
 * Data = 2020/4/17 3:12 下午
 **/

@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
