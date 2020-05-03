package com.learn.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.member.entity.MemberEntity;
import com.learn.gulimall.member.exception.PhoneExistException;
import com.learn.gulimall.member.exception.UserNameExistException;
import com.learn.gulimall.member.vo.MemberLoginVo;
import com.learn.gulimall.member.vo.MemberRegistVo;
import com.learn.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 13:58:56
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo memberRegistVo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUserNameUnique(String userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo loginVo);

    MemberEntity login(SocialUser socialUser) ;
}

