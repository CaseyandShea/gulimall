package com.learn.gulimall.ware.controller;

import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.ware.entity.WmsPurchaseEntity;
import com.learn.gulimall.ware.service.WmsPurchaseService;
import com.learn.gulimall.ware.vo.MergeVo;
import com.learn.gulimall.ware.vo.PurchaseFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 采购信息
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 14:33:02
 */
@RestController
@RequestMapping("ware/wmspurchase")
public class WmsPurchaseController {
    @Autowired
    private WmsPurchaseService wmsPurchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:wmspurchase:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsPurchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    @PostMapping("/merge")
    // @RequiresPermissions("ware:wmspurchase:list")
    public R mergePurchase(@RequestBody MergeVo mergeVo) {
        PageUtils page = wmsPurchaseService.mergePurchase(mergeVo);

        return R.ok().put("page", page);
    }

    @PostMapping("/received")
    // @RequiresPermissions("ware:wmspurchase:list")
    public R receivedList(@RequestBody List<Long> ids) {
        wmsPurchaseService.receivedList(ids);

        return R.ok();
    }

    @PostMapping("/done")
    // @RequiresPermissions("ware:wmspurchase:list")
    public R finishPurchase(@RequestBody PurchaseFinishVo finishVo) {
        wmsPurchaseService.donePurchase(finishVo);

        return R.ok();
    }

    @RequestMapping("/unreceive/list")
    // @RequiresPermissions("ware:wmspurchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsPurchaseService.queryPageUnreceived(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:wmspurchase:info")
    public R info(@PathVariable("id") Long id) {
        WmsPurchaseEntity wmsPurchase = wmsPurchaseService.getById(id);

        return R.ok().put("wmsPurchase", wmsPurchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:wmspurchase:save")
    public R save(@RequestBody WmsPurchaseEntity wmsPurchase) {
        wmsPurchaseService.save(wmsPurchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:wmspurchase:update")
    public R update(@RequestBody WmsPurchaseEntity wmsPurchase) {
        wmsPurchaseService.updateById(wmsPurchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:wmspurchase:delete")
    public R delete(@RequestBody Long[] ids) {
        wmsPurchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
