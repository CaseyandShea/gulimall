package com.learn.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.constant.ProductConstant;
import com.learn.gulimall.common.to.MemberPriceTo;
import com.learn.gulimall.common.to.SkuReductionTo;
import com.learn.gulimall.common.to.SpuBoundTo;
import com.learn.gulimall.common.to.es.AttrModel;
import com.learn.gulimall.common.to.es.SkuEsModel;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.product.dao.SpuInfoDao;
import com.learn.gulimall.product.entity.*;
import com.learn.gulimall.product.feign.CouponFeignService;
import com.learn.gulimall.product.feign.SearchFeignService;
import com.learn.gulimall.product.feign.WareFeignSevice;
import com.learn.gulimall.product.service.*;
import com.learn.gulimall.product.vo.*;
import org.hibernate.validator.internal.util.IdentitySet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService descService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignSevice wareFeignSevice;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfoVo) {
        //1.保存SPU基本信息 pms——spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2.保存SPU描述图片 pms_spu_info_desc
        List<String> decriptList = spuInfoVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decriptList));

        descService.saveSpuInfoDesc(spuInfoDescEntity);

        //3.SPU的图片集 pms_spu_images
        List<String> images = spuInfoVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //4.保存SPU的规格参数 pms_production_attr_value
        //Base
        List<BaseAttrs> baseAttrs = spuInfoVo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr ->
        {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            entity.setAttrName(attrEntity.getAttrName());
            entity.setAttrValue(attr.getAttrValues());
            entity.setQuickShow(attr.getShowDesc());
            entity.setSpuId(spuInfoEntity.getId());
            return entity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntities);
        //5.保存SPU的积分信息
        Bounds bounds = spuInfoVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu 积分信息失败");
        }

        //6.保存当前Spu对应的所有Sku信息
        List<Skus> skus = spuInfoVo.getSkus();
        if (skus != null && skus.size() > 0) {
            //6.1skujiben基本信息 sku_info
            skus.forEach(sku -> {
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) defaultImg = image.getImgUrl();
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.save(skuInfoEntity);


                Long skuId = skuInfoEntity.getSkuId();
                //6.2保存sku的图片信息 pms——sku——images
                List<SkuImagesEntity> skuImagesEntities = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(skuImagesEntity -> {
                    return !StringUtils.isEmpty(skuImagesEntity.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);

                //6.3保存sku的销售属性信息pms_sku_sale_attr_value
                List<Attr> attrList = sku.getAttr();
                List<SkuSaleAttrValueEntity> saleAttrValueEntities = attrList.stream().map(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(saleAttrValueEntities);

                //6.4 保存sku的优惠信息（跨库了）满减
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                List<MemberPriceTo> memberPriceTos = sku.getMemberPrice().stream().map(item -> {
                    MemberPriceTo memberPriceTo = new MemberPriceTo();
                    BeanUtils.copyProperties(item, memberPriceTo);
                    return memberPriceTo;
                }).collect(Collectors.toList());
                skuReductionTo.setMemberPrice(memberPriceTos);
                if (skuReductionTo.getFullCount() > 0
                        || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1
                        || (memberPriceTos != null && memberPriceTos.size() > 0)) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }


            });

        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(
                    wrapper -> {
                        wrapper.eq("id", key).or().like("spu_name", key);
                    }
            );
        }

        String status = params.get("status").toString();
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }
        String bandId = params.get("bandId").toString();
        if (!StringUtils.isEmpty(bandId) && !"0".equals(bandId)) {
            queryWrapper.eq("band_id", bandId);
        }

        String catelogId = params.get("catelogId").toString();
        if (!StringUtils.isEmpty(catelogId) && "0".equals(catelogId)) {
            queryWrapper.eq("catelog_id", catelogId);
        }


        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void upProduct(Long spuId) {
        ArrayList<SkuEsModel> skuEsModels = new ArrayList<>();
        //完成上架
        //组装我们需要的数据

        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuBySpuId(spuId);
        List<Long> skuList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        //查询当前spu的所有可以检索的数据属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrs = attrService.selectSearchAttrs(attrIds);
        Set<Long> idSet = new HashSet<Long>(searchAttrs);
        List<AttrModel> attrModelList = baseAttrs.stream().filter(item -> {
            return searchAttrs.contains(item.getAttrId());
        }).map(attrEntity -> {
            AttrModel attrModel = new AttrModel();
            attrModel.setAttrId(attrEntity.getAttrId());
            attrModel.setAttrName(attrEntity.getAttrName());
            attrModel.setAttrValue(attrEntity.getAttrValue());
            return attrModel;
        }).collect(Collectors.toList());

        //TODO 发送远程调用，库存查询是否有库存
        Map<Long, Boolean> hasStockMap = null;
        try {
            R r = wareFeignSevice.getSkuHasStock(skuList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            List<SkuHasStockVo> hasStockVos = r.getData(typeReference);
            hasStockMap = hasStockVos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        } catch (Exception e) {
            log.error("库存查询出错");
            e.printStackTrace();
        }


        //分装每个sku信息
        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        List<SkuEsModel> skuEsModelList = skuInfoEntities.stream()
                .map(skuInfoEntity -> {
                    SkuEsModel esModel = new SkuEsModel();
                    esModel.setSkuId(skuInfoEntity.getSkuId());
                    esModel.setSpuId(spuId);
                    esModel.setBrandId(skuInfoEntity.getBrandId());
                    esModel.setSkuTitle(skuInfoEntity.getSkuTitle());
                    esModel.setSkuPrice(skuInfoEntity.getPrice());
                    esModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
                    esModel.setSaleCount(skuInfoEntity.getSaleCount());

                    Boolean hasStock = finalHasStockMap == null ? true : finalHasStockMap.get(skuInfoEntity.getSkuId());
                    esModel.setHasStock(hasStock);
                    //ToDo 热度评分

                    //品牌查询
                    BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
                    esModel.setBrandImg(brandEntity.getLogo());
                    esModel.setBrandName(brandEntity.getName());

                    //分类查询
                    CategoryEntity categoryEntity = categoryService.getById(skuInfoEntity.getBrandId());
                    esModel.setCatelogId(categoryEntity.getCatId());
                    esModel.setCatelogName(categoryEntity.getName());

                    esModel.setAttrs(attrModelList);

                    return esModel;
                }).collect(Collectors.toList());

        //TODO 将数据发送非es进行保存 gulimall-search

        R r = searchFeignService.productStatusUp(skuEsModelList);
        if (r.getCode() == 0) {
            //远程调用成功
            //修改状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SUP_UP.getCode());
        } else {
            //远程调用失败
            //todo 重复调用问题   接口幂等性 重试机制
        }


    }


}