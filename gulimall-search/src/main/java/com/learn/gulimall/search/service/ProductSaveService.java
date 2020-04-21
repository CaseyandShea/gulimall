package com.learn.gulimall.search.service;

import com.learn.gulimall.common.to.es.SkuEsModel;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

/**
 * packageName = com.learn.gulimall.search.service
 * author = Casey
 * Data = 2020/4/20 7:59 下午
 **/

@Repository
public interface ProductSaveService {
    Boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
