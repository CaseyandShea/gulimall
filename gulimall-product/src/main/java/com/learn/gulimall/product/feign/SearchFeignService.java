package com.learn.gulimall.product.feign;

import com.learn.gulimall.common.to.es.SkuEsModel;
import com.learn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * packageName = com.learn.gulimall.product.feign
 * author = Casey
 * Data = 2020/4/20 9:14 下午
 **/
@FeignClient("gulimall-search")
public interface SearchFeignService {
    @PostMapping("/product")
     R productStatusUp(List<SkuEsModel> skuEsModels);
}
