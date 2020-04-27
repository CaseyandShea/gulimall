package com.learn.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * packageName = com.learn.gulimall.search.vo
 * author = Casey
 * Data = 2020/4/23 11:10 下午
 **/

/**
 * 封装页面可能传过来的所有东西
 */
@Data
public class SearchParam {
    private String keyWord;
    private Long catelogId;

    /**
     * sort = saleCount_asc/desc
     * sort=skuPrice_asc/desc
     */
    private String sort;

    /**
     * 过滤条件
     * hasStock
     * skuPrince=1_500/_500/500_
     *  品牌id
     *  属性
     *  arrts=1_其他：安卓 &attrs=2_屏幕
     *
     */

    private Integer hasStock;//是否只显示有货的

    private String skuPrice;

    private List<Long> brandId;//品牌id，支持多选

    private  List<String> attrs;

    private Integer pageNum = 1;//页码

    private String queryString;//查询地址
}
