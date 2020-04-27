package com.learn.gulimall.search.feign;

import com.learn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * packageName = com.learn.gulimall.search.feign
 * author = Casey
 * Data = 2020/4/27 5:19 下午
 **/

@FeignClient("gulimall-product")
public interface ProductFeignService {
    @GetMapping("product/attr/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId);

    @GetMapping("product/brand/info/{brandId}")
    public R getBrandsById(@PathVariable("brandId") List<Long> brandIds);
}
