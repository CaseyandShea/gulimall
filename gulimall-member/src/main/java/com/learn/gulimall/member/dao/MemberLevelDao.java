package com.learn.gulimall.member.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learn.gulimall.member.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 会员等级
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 13:58:56
 */
@Mapper
@Repository
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    MemberLevelEntity getDefualtLevel();

}
