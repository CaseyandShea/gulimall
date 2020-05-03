package com.learn.gulimall.product.controller;

import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.common.valid.AddGroup;
import com.learn.gulimall.common.valid.UpdateGroup;
import com.learn.gulimall.product.entity.BrandEntity;
import com.learn.gulimall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 品牌
 * 使用JSR03校验规则
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:19:51
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }



    /**
     * 信息
     */
    @GetMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }


    @GetMapping("/info/{brandIds}")
    // @RequiresPermissions("product:brand:info")
    public R getBrandsById(@PathVariable("brandIds")List<Long>  brandIds) {
       List<BrandEntity>  brand = brandService.getByIds(brandIds);

        return R.ok().put("brand", brand);
    }



    /**
     * 保存
     *
     * @Valid 开启校验
     */
    @PostMapping("/save")
    // @RequiresPermissions("product:brand:save")
    public R save(@Validated(value = {AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult result*/) {
        /*if (result.hasErrors()) {
            Map<String, String> map = new HashMap<>();
            result.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            R.error(400, "提交数据有不合法").put("error", map);
        }*/
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("product:brand:update")
    public R update(@Validated(value = {UpdateGroup.class}) @RequestBody BrandEntity brand) {
        brandService.updateDetail(brand);

        return R.ok();
    }


    /**
     * 修改
     */
    @PostMapping("/update/list")
    // @RequiresPermissions("product:brand:update")
    public R updateBatch(@RequestBody BrandEntity[] brand) {
        brandService.updateBatchById(Arrays.asList(brand));

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
