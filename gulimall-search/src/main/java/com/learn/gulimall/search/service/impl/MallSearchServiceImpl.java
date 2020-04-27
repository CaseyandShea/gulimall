package com.learn.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.learn.gulimall.common.to.es.SkuEsModel;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.search.config.GulimallElasticSearchConfig;
import com.learn.gulimall.search.constant.EsConstant;
import com.learn.gulimall.search.feign.ProductFeignService;
import com.learn.gulimall.search.service.MallSearchService;
import com.learn.gulimall.search.vo.AttrResponseVo;
import com.learn.gulimall.search.vo.BrandVo;
import com.learn.gulimall.search.vo.SearchParam;
import com.learn.gulimall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName = com.learn.gulimall.search.service.impl
 * author = Casey
 * Data = 2020/4/24 12:02 上午
 **/

@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Qualifier("esRestClient")
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam searchParam) {
        // 1动态构建查询请求需要的DSL语句

        SearchRequest searchRequest = buildSearchRequst(searchParam);
        SearchResult result = null;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            result = buildSearchResult(response, searchParam);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 解析Es  查询返回
     *
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
        SearchResult result = new SearchResult();
        /**
         * hit里面查
         */
        //封装所有查询到的商品
        SearchHits hits = response.getHits();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            // 遍历前提
            List<SkuEsModel> skuEsModelList = new ArrayList<>();
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);

                //高亮信息
                if (!StringUtils.isEmpty(searchParam.getKeyWord())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(string);
                }
                skuEsModelList.add(skuEsModel);
            }
            result.setProducts(skuEsModelList);
        }


        /**
         * 聚合信息里面查
         */
        Aggregations aggregations = response.getAggregations();
        //当单所有商品所涉及的属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = aggregations.get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //得到属性id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //得到属性的名字
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //得到所有的属性值
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");

            List<String> attrValues = attr_value_agg.getBuckets().stream().map(item -> {
                String keyAsString = item.getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());

            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);


        //当前商品所涉及到的品牌信息

        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            //得到品牌名字
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);

            //得到品牌图片
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);


        //当前商品所设计到的分类信息
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catelog_agg = aggregations.get("catelog_agg");
        List<? extends Terms.Bucket> buckets = catelog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatelogId(Long.parseLong(keyAsString));

            //得到分类名
            ParsedStringTerms catelog_name_agg = bucket.getAggregations().get("catelog_name_agg");
            String catelogName = catelog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatelogName(catelogName);

            catalogVos.add(catalogVo);

        }
        result.setCatalogs(catalogVos);


        //当前商品所设计到的分页信息
        int total = (int) hits.getTotalHits().value;
        result.setTotal((long) total);

        int totalPages = total % EsConstant.PAGE_SIZE == 0 ? total / EsConstant.PAGE_SIZE : total / EsConstant.PAGE_SIZE + 1;
        result.setTotalPages(totalPages);

        //构建面包屑导航功能  筛选属性
        if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
            List<SearchResult.NavVo> navVos =
                    searchParam.getAttrs().stream().map(attr -> {
                        SearchResult.NavVo navVo = new SearchResult.NavVo();
                        String[] attrInfo = attr.split("_");
                        navVo.setNavValue(attrInfo[1]);
                        try {
                            R info = productFeignService.info(Long.parseLong(attrInfo[0]));
                            if (info.getCode() == 0) {
                                AttrResponseVo data = info.getData("attr", new TypeReference<AttrResponseVo>() {
                                });
                                navVo.setNavName(data.getAttrName());
                            } else {
                                navVo.setNavName(attrInfo[0]);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //拿到所有的条件

                        String linkAddress = replaceQueryString(searchParam.getQueryString(), attr, "attrs");
                        navVo.setLink("http://search.gulimall.com/list.html?" + linkAddress);
                        return navVo;

                    }).collect(Collectors.toList());


            result.setNavs(navVos);
        }
        if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            R r = productFeignService.getBrandsById(searchParam.getBrandId());
            List<BrandVo> brandVoList = r.getData("brand", new TypeReference<List<BrandVo>>() {
            });
            StringBuffer stringBuffer = new StringBuffer();
            String repalceString = searchParam.getQueryString();
            for (BrandVo brand : brandVoList) {
                stringBuffer.append(brand.getName() + ";");
                repalceString = replaceQueryString(repalceString, brand.getBrandId() + "", "brandId");
            }
            navVo.setNavName(stringBuffer.toString());
            navVo.setLink("http://search.gulimall.com/list.html?" + repalceString);
            navs.add(navVo);

        }


        result.setPageNum(searchParam.getPageNum());


        return result;
    }

    private String replaceQueryString(String queryString, String attr, String key) {
        String linkAddress = null;
        try {
            String encode = URLEncoder.encode(attr, "UTF-8");
            encode = encode.replace("+", "%20"); //浏览器对空格编码和Java不一样
            linkAddress = queryString.replace("&" + key + "=" + encode, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkAddress;
    }

    /**
     * 准备请求
     *
     * @param searchParam
     * @return
     */
    private SearchRequest buildSearchRequst(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * 模糊匹配
         */
        //构建bool query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //mast 条件
        if (!StringUtils.isEmpty(searchParam.getKeyWord())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyWord()));
        }

        //构建一个Filter
        //安装三级分类来查
        if (searchParam.getCatelogId() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catelogId", searchParam.getCatelogId()));
        }

        //按照品牌id
        if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }

        //按照所有指定属性查询
        if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
            //每一个都得生成一个Nested的查询
            for (String attrString : searchParam.getAttrs()) {
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                String[] attr = attrString.split("_");
                String attrId = attr[0];
                String[] attrValue = attr[1].split(":");
                queryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                queryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attr", queryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQuery);

            }

        }


        //按照库存是否有进行查询（0 无库存， 有库存）
        if (searchParam.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));

        }

        //按照价格区间
        if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = searchParam.getSkuPrice().split("_");

            if (s.length == 2) {
                rangeQuery.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (searchParam.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(s[0]);
                }
                if (searchParam.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(s[0]);
                }
            }

            boolQuery.filter(rangeQuery);
        }


        sourceBuilder.query(boolQuery);

        //分页排序 高亮

        //排序
        if (!StringUtils.isEmpty(searchParam.getSort())) {
            //sort=hotScore_ase
            String[] sortInfo = searchParam.getSort().split("_");
            SortOrder order = sortInfo[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(sortInfo[0], order);
        }

        //分页
        //利用页码fron = （pageNun -1）*size
        sourceBuilder.from((searchParam.getPageNum() - 1) * EsConstant.PAGE_SIZE);
        sourceBuilder.size(EsConstant.PAGE_SIZE);

        //高亮
        if (!StringUtils.isEmpty(searchParam.getKeyWord())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }


        //聚合分析

        //品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId");
        brand_agg.size(50);
        //加上自聚合
        TermsAggregationBuilder brand_name_agg = AggregationBuilders.terms("brand_name_agg").field("brandName").size(1);
        brand_agg.subAggregation(brand_name_agg);

        TermsAggregationBuilder brand_img_agg = AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1);
        brand_agg.subAggregation(brand_img_agg);

        sourceBuilder.aggregation(brand_agg);

        //分类聚合
        TermsAggregationBuilder catelog_agg = AggregationBuilders.terms("catelog_agg");
        catelog_agg.field("catelog_agg");
        catelog_agg.size(20);

        TermsAggregationBuilder catelog_name_agg = AggregationBuilders.terms("catelog_name_agg").field("catelogName").size(1);
        catelog_agg.subAggregation(catelog_name_agg);
        sourceBuilder.aggregation(catelog_agg);

        //  属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");

        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg");
        attr_id_agg.field("attrs.attrId");
        attr_id_agg.size(50);

        TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1);
        attr_id_agg.subAggregation(attr_name_agg);

        TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(10);
        attr_id_agg.subAggregation(attr_value_agg);

        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);


        System.out.println("DSL" + sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PROUDCT_INDEX}, sourceBuilder);
        return searchRequest;
    }
}
