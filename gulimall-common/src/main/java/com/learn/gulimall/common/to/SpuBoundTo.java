package com.learn.gulimall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * packageName = com.learn.gulimall.common.to
 * author = Casey
 * Data = 2020/4/16 9:08 下午
 **/
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
