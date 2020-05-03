package com.learn.gulimall.product.web;

import com.learn.gulimall.product.service.SkuInfoService;
import com.learn.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * packageName = com.learn.gulimall.product.web
 * author = Casey
 * Data = 2020/4/27 11:21 下午
 **/

@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;


    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {
        SkuItemVo skuItemVo = skuInfoService.getItem(skuId);
        model.addAttribute("item", skuItemVo);
        //准备查询商品服务
        return "item";
    }
}
