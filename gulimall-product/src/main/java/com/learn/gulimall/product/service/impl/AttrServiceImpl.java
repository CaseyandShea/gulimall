package com.learn.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.constant.ProductConstant;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.learn.gulimall.product.dao.AttrDao;
import com.learn.gulimall.product.dao.AttrGroupDao;
import com.learn.gulimall.product.dao.CategoryDao;
import com.learn.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.learn.gulimall.product.entity.AttrEntity;
import com.learn.gulimall.product.entity.AttrGroupEntity;
import com.learn.gulimall.product.entity.CategoryEntity;
import com.learn.gulimall.product.service.AttrService;
import com.learn.gulimall.product.service.CategoryService;
import com.learn.gulimall.product.vo.AttrGroupRelationVo;
import com.learn.gulimall.product.vo.AttrRespVo;
import com.learn.gulimall.product.vo.AttrVo;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存商品属性AttrEntity
     *
     * @param attr
     */
    @Override
    @Transactional
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);

        //保存基本数据
        this.save(attrEntity);

        //保存关联关系
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(relationEntity);
        }
    }

    @Override
    @Transactional
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catlogId, String type) {
        int attr_type_saleCode = "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode();
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", attr_type_saleCode);
        if (catlogId != 0) {
            queryWrapper.eq("catelog_id", catlogId);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("attr_id", key).or().like("attr_name", key);
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> attrRespVos = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //设置分类和分组的名字
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrEntity.getAttrId()));
            //只有是基本信息和有关联才会加入分组信息
            if ("base".equalsIgnoreCase(type)
                    && relationEntity != null
                    && relationEntity.getAttrGroupId() != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(attrRespVos);
        return pageUtils;
    }

    /**
     * d
     *
     * @return AttrRespVo
     */
    @Override
    @Transactional
    public AttrRespVo getAttrInfo(Long attrId) {
        //1.查询AttrEntity信息
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        //分组信息设置
        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrEntity.getAttrId()));
        if (relationEntity != null) {
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
            attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
        }

        //设置分类信息
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);
        //查询分组信息
        return attrRespVo;
    }

    /**
     * 修改分组
     *
     * @param attr 页面传上来的数据
     */
    @Transactional
    @Override
    public void updateAttr(AttrRespVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        //1.修改关联分组(是基本类型才需要修改分组)
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            Integer selectCount = attrAttrgroupRelationDao.selectCount(new UpdateWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attr.getAttrId()));
            if (selectCount > 0) {
                attrAttrgroupRelationDao.update(
                        attrAttrgroupRelationEntity,
                        new UpdateWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id", attr.getAttrId())
                );
            } else {
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }

    }

    /**
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

        //属性ID集合
        List<Long> attrIds = relationEntities.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        //根据属性id集合查出所有集合
        if (attrIds == null || attrIds.size() == 0) {
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);

        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        // attrAttrgroupRelationDao.delete();
        // 批量删除
        List<AttrAttrgroupRelationEntity> relationEntities = Arrays.asList(vos).stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(relationEntities);
    }

    /**
     * 超
     *
     * @param attrgroupId
     * @param params
     * @return
     */
    @Override
    public PageUtils getNotRelationAttr(Long attrgroupId, Map<String, Object> params) {
        //1.获取当前分组所属的分类
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        //2.当前分类只能关联别的分组没有引用的属性
        //2.1当前分类下的分组
        val groupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        List<Long> groupList = groupEntities.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        //2.2这些分组所关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupList));

        List<Long> attrIds = relationEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        //2.3从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId);
        //查出已经关联的属性，如果存在已经关联了的化就直接添加
        if (attrIds != null || attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

}