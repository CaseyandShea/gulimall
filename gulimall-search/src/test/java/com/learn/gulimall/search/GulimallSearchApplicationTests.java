package com.learn.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.learn.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;

/**
 * packageName = com.learn.gulimall.search
 * author = Casey
 * Data = 2020/4/20 1:55 下午
 **/
@SpringBootTest
public class GulimallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    @Test
    void searchData() throws IOException {
        //创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构造检索条件
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address", "mill");
        searchSourceBuilder.query(matchQueryBuilder);

//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//
        //聚合
        //按照年龄值聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(termsAggregationBuilder);
//        searchSourceBuilder.aggregation();
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchRequest);


        //执行检索
        SearchResponse search = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);


        //分析结果
        System.out.println(search);
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            String stringJson = hit.getSourceAsString();
            Account account = JSON.parseObject(stringJson, Account.class);
            System.out.println(account);
        }

        Aggregations aggregations = search.getAggregations();
       /* for (Aggregation aggregation :
                aggregations.asList()) {

            System.out.println("当前聚合" + aggregation.getName());


        }*/
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄" + keyAsString + "===>" + bucket.getDocCount());
        }

        Avg avg = aggregations.get("balanceAvg");
        System.out.println("平均薪资" + avg.getValue());


    }

    /**
     * 存储数据到es
     */
    @Test
    void indexLoads() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
//        indexRequest.source("userName","zhangsan","age",18,"gender","man");

        User user = new User();
        user.age = 14;
        user.gender = "男";
        user.userName = "sdd";
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);
        IndexResponse index = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

        //提取有用的数据
        System.out.println(index);
    }

    @ToString
    @Data
    class User {
        private String userName;
        private String gender;
        private Integer age;
    }

    /**
     * Copyright 2020 bejson.com
     */

    /**
     * Auto-generated: 2020-04-20 15:41:17
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    @Data
    public static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }
}
