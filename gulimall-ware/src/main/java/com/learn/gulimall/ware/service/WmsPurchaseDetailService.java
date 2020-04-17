package com.learn.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.ware.entity.WmsPurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 14:33:02
 */
public interface WmsPurchaseDetailService extends IService<WmsPurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<WmsPurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

