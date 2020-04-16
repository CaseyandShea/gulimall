package com.learn.gulimall.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * packageName = com.learn.gulimall.common.to
 * author = Casey
 * Data = 2020/4/16 9:20 下午
 **/
@Data
public class SkuReductionTo {
    private Long skuId;
    private Integer fullCount;
    private BigDecimal discount;
    private Integer countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPriceTo> memberPrice;
}
