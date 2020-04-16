package com.learn.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/16 5:54 下午
 **/
@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}
