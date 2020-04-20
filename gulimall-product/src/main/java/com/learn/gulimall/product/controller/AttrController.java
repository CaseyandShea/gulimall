package com.learn.gulimall.product.controller;

import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.product.entity.AttrEntity;
import com.learn.gulimall.product.entity.ProductAttrValueEntity;
import com.learn.gulimall.product.service.AttrService;
import com.learn.gulimall.product.service.ProductAttrValueService;
import com.learn.gulimall.product.vo.AttrGroupRelationVo;
import com.learn.gulimall.product.vo.AttrRespVo;
import com.learn.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 商品属性
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:19:51
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;


    @Autowired
    ProductAttrValueService productAttrValueService;

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> datas = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok();
    }


    @GetMapping("/ {type}/list/{catlogId}")
    public R getBaseList(@PathVariable("catlogId") Long catlogId,
                         @RequestParam Map<String, Object> params,
                         @PathVariable("type") String type) {
        attrService.queryBaseAttrPage(params, catlogId, type);

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId) {
//        AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrRespVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }

    @PostMapping("/update/{spuId}")
    // @RequiresPermissions("product:attr:update")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,
                           @RequestBody List<ProductAttrValueEntity> attrValueEntities) {
        productAttrValueService.updateProductAttr(spuId,attrValueEntities);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }


}
