package com.learn.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.product.dao.SkuInfoDao;
import com.learn.gulimall.product.entity.SkuImagesEntity;
import com.learn.gulimall.product.entity.SkuInfoEntity;
import com.learn.gulimall.product.entity.SpuInfoDescEntity;
import com.learn.gulimall.product.entity.SpuInfoEntity;
import com.learn.gulimall.product.service.*;
import com.learn.gulimall.product.vo.ItemSaleAttrVo;
import com.learn.gulimall.product.vo.SkuItemVo;
import com.learn.gulimall.product.vo.SpuItemAttrGroupVo;
import io.netty.util.concurrent.CompleteFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService imagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;


    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService attrValueService;

    @Autowired
    ThreadPoolExecutor executor;


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

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        return skuInfoEntities;
    }

    /**
     * 获取的item信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVo getItem(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        //1.获取Sku基本信息
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuinfo = getById(skuId);
            skuItemVo.setInfo(skuinfo);
            return skuinfo;
        }, executor);
        //2.sku图片信息
        CompletableFuture<Void> skuImageFuture = skuInfoFuture.thenAcceptAsync((resu) -> {
            Long spuId = resu.getSpuId();
            List<ItemSaleAttrVo> saleAttrVos = attrValueService.getSaleAttrBySpuId(spuId);
            skuItemVo.setSaleAttrVos(saleAttrVos);

        }, executor);


        //spu信息
        CompletableFuture<Void> spuFuture = skuInfoFuture.thenAcceptAsync((resu) -> {
            Long spuId = resu.getSpuId();
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(skuId);
            skuItemVo.setDesp(spuInfoDescEntity);

        }, executor);

        CompletableFuture<Void> spuDesFuture = CompletableFuture.runAsync(() -> {
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(skuId);
            skuItemVo.setDesp(spuInfoDescEntity);
        }, executor);


        CompletableFuture<Void> attrGroup = skuInfoFuture.thenAcceptAsync((resu) -> {
            Long spuId = resu.getSpuId();
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrBySpuId(resu.getCatalogId(), spuId);
            skuItemVo.setGroupVos(attrGroupVos);
        }, executor);

        CompletableFuture<Void> future = CompletableFuture.allOf(skuImageFuture, spuFuture, spuDesFuture, attrGroup);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return skuItemVo;
    }

    public SkuItemVo getIte(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        //1.获取Sku基本信息
        SkuInfoEntity skuinfo = getById(skuId);
        skuItemVo.setInfo(skuinfo);
        //2.sku图片信息
        List<SkuImagesEntity> imagesEntities = imagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(imagesEntities);

        //2. 获取spu的销售属性
        Long spuId = skuinfo.getSpuId();
        List<ItemSaleAttrVo> saleAttrVos = attrValueService.getSaleAttrBySpuId(spuId);
        skuItemVo.setSaleAttrVos(saleAttrVos);

        //获取spu介绍
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(skuId);
        skuItemVo.setDesp(spuInfoDescEntity);

        //获取spu的规格介绍\\
        List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrBySpuId(skuinfo.getCatalogId(), spuId);
        skuItemVo.setGroupVos(attrGroupVos);

        return skuItemVo;
    }

}