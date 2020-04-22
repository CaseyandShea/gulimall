package com.learn.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.product.dao.CategoryDao;
import com.learn.gulimall.product.entity.CategoryEntity;
import com.learn.gulimall.product.service.CategoryBrandRelationService;
import com.learn.gulimall.product.service.CategoryService;
import com.learn.gulimall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params)
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //装配树形结构
        return entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((meun -> {
            meun.setChildren(findChildren(meun, entities));
            return meun;
        })).sorted((category1, category2) -> {
            return (category1.getSort() == null ? 0 : category1.getSort()) - (category2.getSort() == null ? 0 : category2.getSort());
        }).collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        //todo 检查当前删除的菜单，是否被其他地方引用
        baseMapper.deleteBatchIds(catIds);
    }

    /**
     * 找发哦catelogId的完整路径
     *
     * @param catelogId
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 极联更新所以数据
     * <p>
     * 是一个事物
     * 只有开启事物才能使用
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0)
        );
        return categoryEntities;
    }

    @Override
    public  Map<String, List<Catelog2Vo>> getCatelogJson() {
        //1.查出所有一级分类
        List<CategoryEntity> categoryEntities = listWithTree();
        //2。封装数据
        Map<String, List<Catelog2Vo>> stringListMap = categoryEntities.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            List<Catelog2Vo> collect = v.getChildren().stream().map(categoryEntity -> {
                Catelog2Vo catelog2Vo = new Catelog2Vo();
                catelog2Vo.setCatalog1Id(v.getCatId().toString());
                catelog2Vo.setId(categoryEntity.getCatId().toString());
                catelog2Vo.setName(categoryEntity.getName());
                List<Catelog2Vo.Catelog3Vo> catelog3Vos = categoryEntity.getChildren().stream().map(ev -> {
                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo();
                    catelog3Vo.setCatalog2Id(ev.getParentCid().toString());
                    catelog3Vo.setId(ev.getCatId().toString());
                    catelog3Vo.setName(ev.getName());
                    return catelog3Vo;
                }).collect(Collectors.toList());
                catelog2Vo.setCatalog3List(catelog3Vos);
                return catelog2Vo;

            }).collect(Collectors.toList());
            return collect;
        }));

        return stringListMap;
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //手机当单接单
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getCatId(), paths);
        }
        return paths;
    }

    private List<CategoryEntity> findChildren(CategoryEntity meun, List<CategoryEntity> all) {
        return all.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == meun.getCatId()
        ).map(categoryEntity -> {
            categoryEntity.setChildren(findChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((category1, category2) -> {
            return (category1.getSort() == null ? 0 : category1.getSort()) - (category2.getSort() == null ? 0 : category2.getSort());
        }).collect(Collectors.toList());
    }

}