package com.learn.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.constant.WareConstant;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.ware.dao.WmsPurchaseDao;
import com.learn.gulimall.ware.entity.WmsPurchaseDetailEntity;
import com.learn.gulimall.ware.entity.WmsPurchaseEntity;
import com.learn.gulimall.ware.service.WmsPurchaseDetailService;
import com.learn.gulimall.ware.service.WmsPurchaseService;
import com.learn.gulimall.ware.service.WmsWareSkuService;
import com.learn.gulimall.ware.vo.MergeVo;
import com.learn.gulimall.ware.vo.PurchaseFinishVo;
import com.learn.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("wmsPurchaseService")
public class WmsPurchaseServiceImpl extends ServiceImpl<WmsPurchaseDao, WmsPurchaseEntity> implements WmsPurchaseService {

    @Autowired
    WmsPurchaseDetailService purchaseDetailService;

    @Autowired
    WmsWareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WmsPurchaseEntity> queryWrapper = new QueryWrapper<>();
        String key = params.get("key").toString();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("parchase_id", key).or().eq("sku_id", key);
            });
        }
        String status = params.get("status").toString();
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("status", status);
        }

        String wareId = params.get("wareId").toString();
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceived(Map<String, Object> params) {
        QueryWrapper<WmsPurchaseEntity> queryWrapper = new QueryWrapper<WmsPurchaseEntity>().eq("status", 0).or().eq("status", 1);
        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public PageUtils mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            WmsPurchaseEntity purchaseEntity = new WmsPurchaseEntity();
            purchaseEntity.setStatus(0);
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setCreateTime(new Date());
            purchaseId = purchaseEntity.getId();
            this.save(purchaseEntity);
        }

        Long finalPurchaseId = purchaseId;
        List<WmsPurchaseDetailEntity> collect = mergeVo.getItems().stream().map(i -> {
            WmsPurchaseDetailEntity purchaseDetailEntity = new WmsPurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetialStateEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);
        WmsPurchaseEntity purchaseEntity = new WmsPurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

        return null;
    }

    @Override
    public void receivedList(List<Long> ids) {
        //1.确认采购单状态是（新建或者是已分配）
        List<WmsPurchaseEntity> purchaseEntities = this.listByIds(ids).stream().filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStateEnum.CREATED.getCode()
                    || item.getStatus() == WareConstant.PurchaseStateEnum.CREATED.getCode()) return true;
            return false;
        }).map(item -> {
            WmsPurchaseEntity wmsPurchaseEntity = new WmsPurchaseEntity();
            wmsPurchaseEntity.setId(item.getId());
            wmsPurchaseEntity.setStatus(WareConstant.PurchaseStateEnum.RECEIVE.getCode());
            wmsPurchaseEntity.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        // 2.改变采购单状态
        this.updateBatchById(purchaseEntities);

        //3.改变采购项的状态
        for (WmsPurchaseEntity purchaseEntity :
                purchaseEntities) {
            List<WmsPurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listDetailByPurchaseId(purchaseEntity.getId());
            Collection<WmsPurchaseDetailEntity> purchaseDetailEntities1 = purchaseDetailEntities.stream().map(
                    entity -> {
                        WmsPurchaseDetailEntity detailEntity = new WmsPurchaseDetailEntity();
                        detailEntity.setId(entity.getId());
                        detailEntity.setStatus(WareConstant.PurchaseDetialStateEnum.BUYING.getCode());
                        return detailEntity;
                    }
            ).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities1);
        }

    }

    @Override
    public void donePurchase(PurchaseFinishVo finishVo) {
        //  改变采购单的状态

        //2。采购项的状态
        List<PurchaseItemDoneVo> items = finishVo.getItems();
        List<WmsPurchaseDetailEntity> upateitems = new ArrayList<WmsPurchaseDetailEntity>();
        boolean flag = true;
        for (PurchaseItemDoneVo itemDoneVo :
                items) {
            WmsPurchaseDetailEntity purchaseDetailEntity = new WmsPurchaseDetailEntity();
            if (itemDoneVo.getStatus() == WareConstant.PurchaseDetialStateEnum.HAS_ERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(itemDoneVo.getStatus());
            }
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetialStateEnum.FINSH.getCode());

            purchaseDetailEntity.setId(itemDoneVo.getItemId());
            upateitems.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(upateitems);

        WmsPurchaseEntity purchaseEntity = new WmsPurchaseEntity();
        purchaseEntity.setId(finishVo.getId());
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStateEnum.FINSH.getCode() : WareConstant.PurchaseStateEnum.HAS_ERROR.getCode());

        this.updateById(purchaseEntity);


       
    }
}

