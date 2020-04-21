package com.learn.gulimall.search.controller;

import com.learn.gulimall.common.exception.BizCodeEnum;
import com.learn.gulimall.common.to.es.SkuEsModel;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * packageName = com.learn.gulimall.search.controller
 * author = Casey
 * Data = 2020/4/20 7:56 下午
 **/

@RequestMapping("/search")
@RestController
@Slf4j
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;
    boolean b = false;

    @PostMapping("/product")
    public R productStatusUp(List<SkuEsModel> skuEsModels) {
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        } catch (Exception e) {
            log.error("商品上架错误");
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }


        return b ? R.ok() : R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());


    }
}
