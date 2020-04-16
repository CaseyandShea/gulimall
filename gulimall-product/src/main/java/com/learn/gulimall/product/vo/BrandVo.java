package com.learn.gulimall.product.vo;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/16 4:41 下午
 **/
@Data
public class BrandVo {
    /**
     * 品牌Id
     */
    private Long brandId;

    /**
     * 品牌name
     */
    private String brandName;
}
