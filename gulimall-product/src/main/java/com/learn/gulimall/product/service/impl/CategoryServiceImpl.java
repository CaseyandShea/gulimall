package com.learn.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.product.dao.CategoryDao;
import com.learn.gulimall.product.entity.CategoryEntity;
import com.learn.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
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