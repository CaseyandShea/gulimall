package com.learn.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName = com.learn.gulimall.product.vo
 * author = Casey
 * Data = 2020/4/21 2:28 下午
 **/

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {
    private List<Catelog3Vo> catalog3List; //三级子分类
    private String catalog1Id;//一级分类的Id
    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo {
        private String catalog2Id; //二级分类id
        private String id;
        private String name;
    }
}
