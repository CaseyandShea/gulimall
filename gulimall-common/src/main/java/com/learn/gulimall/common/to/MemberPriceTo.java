package com.learn.gulimall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * packageName = com.learn.gulimall.common.to
 * author = Casey
 * Data = 2020/4/16 9:22 下午
 **/
@Data
public class MemberPriceTo {
    private Long id;
    private String name;
    private BigDecimal price;
}
