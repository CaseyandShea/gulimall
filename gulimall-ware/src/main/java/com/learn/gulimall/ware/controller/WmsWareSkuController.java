package com.learn.gulimall.ware.controller;

import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.ware.entity.WmsWareSkuEntity;
import com.learn.gulimall.ware.service.WmsWareSkuService;
import com.learn.gulimall.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品库存
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 14:33:02
 */
@RestController
@RequestMapping("ware/wmswaresku")
public class WmsWareSkuController {
    @Autowired
    private WmsWareSkuService wmsWareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:wmswaresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsWareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }

    @PostMapping("/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> vos = wmsWareSkuService.getSkusHasStock(skuIds);
        R r = R.ok();
        r.setData(vos);
        return r;
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:wmswaresku:info")
    public R info(@PathVariable("id") Long id) {
        WmsWareSkuEntity wmsWareSku = wmsWareSkuService.getById(id);

        return R.ok().put("wmsWareSku", wmsWareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:wmswaresku:save")
    public R save(@RequestBody WmsWareSkuEntity wmsWareSku) {
        wmsWareSkuService.save(wmsWareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:wmswaresku:update")
    public R update(@RequestBody WmsWareSkuEntity wmsWareSku) {
        wmsWareSkuService.updateById(wmsWareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:wmswaresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wmsWareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
