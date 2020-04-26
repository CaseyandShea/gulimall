package com.learn.gulimall.search.controller;

import com.learn.gulimall.common.exception.BizCodeEnum;
import com.learn.gulimall.common.to.es.AttrModel;
import com.learn.gulimall.common.to.es.SkuEsModel;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        skuEsModels = skuEsModels != null ? skuEsModels : new ArrayList<SkuEsModel>();
        for (Long i = 1l; i<9 ;i++){
            SkuEsModel esModel = new SkuEsModel();
            AttrModel attrModel = new AttrModel();
            attrModel.setAttrValue("海思（hisilicon）");
            attrModel.setAttrName("CPU品牌");
            attrModel.setAttrId(15L);

            AttrModel attrModel1 = new AttrModel();
            attrModel1.setAttrValue("HUAWEI Kirin 970");
            attrModel1.setAttrName("CPU型号");
            attrModel1.setAttrId(16L);
            ArrayList<AttrModel> attrModels = new ArrayList<>();
            attrModels.add(attrModel);
            attrModels.add(attrModel1);

            esModel.setAttrs(attrModels);
            esModel.setBrandId(9l);
            esModel.setBrandImg("");
            esModel.setBrandName("华为");
            esModel.setCatelogId(225l);
            esModel.setCatelogName("手机");
            esModel.setHasStock(true);

            esModel.setSkuId(i);
            esModel.setSkuImg("");
            esModel.setSkuPrice(new BigDecimal(6299));
            esModel.setSkuTitle("华为 HUAWEI Mate 30 pro 星河银 8G+256麒麟990旗舰芯片OLED环屏双4000万莱卡电影四摄4G全网通手机");
            esModel.setSpuId(11l);

        }



        try {
            b = productSaveService.productStatusUp(skuEsModels);
        } catch (Exception e) {
            log.error("商品上架错误");
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }


        return b ? R.ok() : R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());


    }
}
