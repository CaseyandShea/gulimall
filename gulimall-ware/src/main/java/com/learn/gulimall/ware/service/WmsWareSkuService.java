package com.learn.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.ware.entity.WmsWareSkuEntity;
import com.learn.gulimall.ware.vo.SkuHasStockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 14:33:02
 */
public interface WmsWareSkuService extends IService<WmsWareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);
}

