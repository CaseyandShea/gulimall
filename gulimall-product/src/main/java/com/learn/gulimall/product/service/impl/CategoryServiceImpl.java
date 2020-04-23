package com.learn.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

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

    /*

     清楚多个
    @Caching(evict = {
            @CacheEvict(value = {"category"},key = "'getLevel1Categorys'"),
            @CacheEvict(value = {"category"},key = "'getCatelogJson'")
    })*/

    @CacheEvict(value = {"category"},allEntries = true) //删除一个分区下的所有数据
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }
    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0)
        );
        return categoryEntities;
    }

    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        return getCatelogJsonFromDb();
    }


    //每一个需要缓存的数据我们都要指定放到那个名字的缓存

    public Map<String, List<Catelog2Vo>> getCatelogJson1() {
        //加入缓存逻辑，缓存的数据是json字符串
        //json跨语言跨平台兼容
        /**
         * 1。空结果缓存：解决缓存穿透
         * 2，设置过期时间+随机值 ：解决缓存雪崩
         * 3.加锁解决缓存击穿问题
         */

        String catelogJson = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catelogJson)) {
            Map<String, List<Catelog2Vo>> catelogJsonFromDb = getCatelogJsonFromWithRedisDb();
            return catelogJsonFromDb;
        }

        Map<String, List<Catelog2Vo>> stringListMap = JSON.parseObject(
                catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
        return stringListMap;

    }


    private Map<String, List<Catelog2Vo>> getCatelogJsonFromWithRedisDb() {
        //抢占分布式锁
      /*  Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "111");
        redisTemplate.expire("lock",30, TimeUnit.SECONDS);*/
        // 使用原子操作占锁
       /* String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);*/

        RLock lock = redissonClient.getLock("my-lock");
//        lock.lock();//阻塞式等待
        lock.lock(30, TimeUnit.SECONDS);//30s后自动解锁  就没有自动续期了  自动解锁时间一定要大于业务执行时间   不会自动续期
        Map<String, List<Catelog2Vo>> catelogJsonFromDb = null;
        try {
            catelogJsonFromDb = getCatelogJsonFromDb();
        } finally {
            lock.unlock();
        }

        /*  *//* if (lock) {

            try {
                catelogJsonFromDb = getCatelogJsonFromDb();

            } finally {
                String script = "if redis.call('get',KEYS[1]) == ARGV(1) then return redis.call('del',KEYS[1]) else return 0  end";
                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList("lock"), uuid);
            }*//*
         *//* String lock1 = redisTemplate.opsForValue().get("lock");
            if (uuid.equals(lock1)){
                redisTemplate.delete("lock");
            }
            请求得到lock 去删之前过期了，别人正好占锁了  就容易出现错误 删掉别人的锁

            所以删锁也需要保证原子性
            *//*

        return catelogJsonFromDb;
    } else

    {
        //休眠一下
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //自旋
        return catelogJsonFromDb;
    }


    private Map<String, List<Catelog2Vo>> getCatelogJsonFromDb() {
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
        String stringJson = JSON.toJSONString(stringListMap);
        redisTemplate.opsForValue().set("catalogJSON", stringJson);
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