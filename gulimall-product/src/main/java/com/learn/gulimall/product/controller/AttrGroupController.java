package com.learn.gulimall.product.controller;

import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.product.entity.AttrEntity;
import com.learn.gulimall.product.entity.AttrGroupEntity;
import com.learn.gulimall.product.service.AttrAttrgroupRelationService;
import com.learn.gulimall.product.service.AttrGroupService;
import com.learn.gulimall.product.service.AttrService;
import com.learn.gulimall.product.service.CategoryService;
import com.learn.gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 属性分组
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 10:19:51
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrService attrService;


    @Autowired
    AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable Long catelogId) {
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();

        Long[] paths = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(paths);


        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", entities);
    }

    /**
     * 获取当前的group 可以关联的属性
     *
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNotRelation(@PathVariable("attrgroupId") Long attrgroupId,
                             @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.getNotRelationAttr(attrgroupId, params);
        return R.ok().put("data", page);
    }

    /**
     * 删除关联分组信息
     * RequestBody请求Body
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos) {
        attrService.deleteRelation(vos);

        return R.ok();
    }


    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> attrGroupRelationVos) {

        relationService.saveBatchRelation(attrGroupRelationVos);
        return R.ok();
    }

}
