package com.learn.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.product.dao.SkuInfoDao;
import com.learn.gulimall.product.entity.SkuInfoEntity;
import com.learn.gulimall.product.entity.SpuInfoEntity;
import com.learn.gulimall.product.service.SkuInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageCondition(Map<String, Object> params) {

        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(
                    wrapper -> {
                        wrapper.eq("sku_id", key).or().like("sku_name", key);
                    }
            );
        }
        String catelogId = params.get("catelogId").toString();
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        //品牌id
        String bandId = params.get("bandId").toString();
        if (!StringUtils.isEmpty(bandId) && !bandId.equals("0")) {
            queryWrapper.eq("band_id", bandId);
        }

        String min = params.get("min").toString();
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = params.get("max").toString();
        if (!StringUtils.isEmpty(max)) {
            try {
                if ((new BigDecimal(max)).compareTo(new BigDecimal(0)) == 1)
                    queryWrapper.le("price", min);
            } catch (Exception e) {

            }

        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

}