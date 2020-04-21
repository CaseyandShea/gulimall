package com.learn.gulimall.product.feign;

import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.product.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * packageName = com.learn.gulimall.product.feign
 * author = Casey
 * Data = 2020/4/20 7:05 下午
 **/
@FeignClient("gulimall-ware")
public interface WareFeignSevice {
    /**
     * 1.在R里面加上泛型
     * 2.直接返回想要的结果 List<SkuHasStockVo></> getSkuHasStock(@RequestBody List<Long> skuIds)
     * 3.自己分装返回结构
     *
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
