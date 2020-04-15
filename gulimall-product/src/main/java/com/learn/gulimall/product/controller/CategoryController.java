package com.learn.gulimall.product.controller;

import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.product.entity.CategoryEntity;
import com.learn.gulimall.product.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 商品三级分类
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:19:51
 */
@Api(value = "商品分类管理")
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类平以树形结构组装列表
     */
    @ApiOperation(value = "查询所有分类平以树形结构组装列表")
    @GetMapping("/list/tree")
    // @RequiresPermissions("product:category:list")
    public R list() {
//        PageUtils page = categoryService.queryPage(params);
        List<CategoryEntity> entityList = categoryService.listWithTree();

        return R.ok().put("data", entityList);
    }


    /**
     * 信息
     */
    @ApiOperation(value = "根据CatID查询分类信息")
    @GetMapping("/info/{catId}")
    // @RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 添加Category
     */
    @ApiOperation(value = "新增Category信息")
    @PostMapping("/save")
    // @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 单个修改修改
     */
    @PostMapping("/update")
    // @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateById(category);

        categoryService.updateCascade(category);
        return R.ok();
    }

    /**
     * 单个修改修改
     */
    @PostMapping("/update/sort")
    // @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity[] categorys) {
        categoryService.updateBatchById(Arrays.asList(categorys));
        return R.ok();
    }


    /**
     * 批量删除删除
     *
     * @RequestBoy 获取请求体，必须发送Post请求
     * springMVc自动将请求体的数据（json），转为对应的对象
     */
    @PostMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds) {
        //1.检查当前菜单是否被引用
        categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
