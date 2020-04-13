package com.learn.gulimall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.gulimall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 14:13:01
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

}
