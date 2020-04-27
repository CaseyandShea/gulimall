package com.learn.gulimall.search.vo;

import com.learn.gulimall.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName = com.learn.gulimall.search.vo
 * author = Casey
 * Data = 2020/4/24 12:23 上午
 **/
@Data
public class SearchResult {
    private List<SkuEsModel> products; //查询到的商品信息


    /**
     * 分页信息
     */
    private Integer pageNum;//当前页

    private Long total; //总共的数量

    private Integer totalPages; //一共多少页

    private List<BrandVo> brands; //检查到的商品所涉机的平排

    private List<AttrVo> attrs;//查询到的结果所涉及到的所有属性

    private List<CatalogVo> catalogs;//所有分类

    //面包屑导航
    private List<NavVo> navs = new ArrayList<>();

    @Data
    public static class NavVo{

        private String navName;
        private String navValue;
        private String  link;
    }

    /**
     *
     */
    @Data
    public static class   BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class   AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class   CatalogVo{
        private Long catelogId;
        private String catelogName;
    }
}
