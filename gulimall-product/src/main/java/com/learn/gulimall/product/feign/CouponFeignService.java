package com.learn.gulimall.product.feign;

import com.learn.gulimall.common.to.SkuReductionTo;
import com.learn.gulimall.common.to.SpuBoundTo;
import com.learn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * packageName = com.learn.gulimall.product.feign
 * author = Casey
 * Data = 2020/4/16 8:56 下午
 **/

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /**
     * 第一步：将对象转为json
     * 第二部：找到gulimall-coupon服务 给发发请求
     * 对方服务收到请求，请求体里有json
     *
     * @param spuBoundTo
     * @return
     * @RequestBody SpuBoundTo spuBoundTo 请求体的json转为SpuBoundTo对象
     * <p>
     * 只要Json数据模型是兼容的  双发服务无需使用同一个to
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
