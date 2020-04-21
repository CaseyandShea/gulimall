package com.learn.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.learn.gulimall.common.to.es.SkuEsModel;
import com.learn.gulimall.search.config.GulimallElasticSearchConfig;
import com.learn.gulimall.search.constant.EsConstant;
import com.learn.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName = com.learn.gulimall.search.service.impl
 * author = Casey
 * Data = 2020/4/20 8:02 下午
 **/
@Slf4j
@Service("productSaveService")
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //1.给es中建立索引  product 建立好映射关系

        //批量保存
        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel sku : skuEsModels) {

            IndexRequest indexRequest = new IndexRequest(EsConstant.PROUDCT_INDEX);
            String s = JSON.toJSONString(sku);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        bulk.hasFailures();//是否有错误
        if (bulk.hasFailures()) {
            List<String> collect = Arrays.stream(bulk.getItems()).filter(item -> {
                return item.isFailed();
            }).map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            log.error("商品上架出现错误\n出现错误的skuId为" + collect.toString());
        }

        return bulk.hasFailures();
    }
}
