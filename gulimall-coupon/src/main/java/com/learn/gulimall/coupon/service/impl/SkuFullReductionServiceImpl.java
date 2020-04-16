package com.learn.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.to.MemberPriceTo;
import com.learn.gulimall.common.to.SkuReductionTo;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.coupon.dao.SkuFullReductionDao;
import com.learn.gulimall.coupon.entity.MemberPriceEntity;
import com.learn.gulimall.coupon.entity.SkuFullReductionEntity;
import com.learn.gulimall.coupon.entity.SkuLadderEntity;
import com.learn.gulimall.coupon.service.MemberPriceService;
import com.learn.gulimall.coupon.service.SkuFullReductionService;
import com.learn.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //sku优惠信息
        if (skuReductionTo.getFullCount() > 0) {
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
            skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
            skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());

            skuLadderService.save(skuLadderEntity);
        }

        //满减信息
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
            this.save(skuFullReductionEntity);
        }


        //会员价格
        List<MemberPriceTo> memberPrices = skuReductionTo.getMemberPrice();
        if (memberPrices != null && memberPrices.size() > 0) {
            List<MemberPriceEntity> memberPriceEntities = memberPrices.stream().map(memberPriceTo -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                memberPriceEntity.setMemberLevelId(memberPriceTo.getId());
                memberPriceEntity.setMemberLevelName(memberPriceTo.getName());
                memberPriceEntity.setMemberPrice(memberPriceTo.getPrice());
                return memberPriceEntity;
            }).collect(Collectors.toList());

            memberPriceService.saveBatch(memberPriceEntities);
        }


    }

}