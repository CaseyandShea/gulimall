package com.learn.gulimall.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learn.gulimall.common.utils.HttpUtils;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.Query;
import com.learn.gulimall.member.dao.MemberDao;
import com.learn.gulimall.member.dao.MemberLevelDao;
import com.learn.gulimall.member.entity.MemberEntity;
import com.learn.gulimall.member.entity.MemberLevelEntity;
import com.learn.gulimall.member.exception.PhoneExistException;
import com.learn.gulimall.member.exception.UserNameExistException;
import com.learn.gulimall.member.service.MemberService;
import com.learn.gulimall.member.vo.MemberLoginVo;
import com.learn.gulimall.member.vo.MemberRegistVo;
import com.learn.gulimall.member.vo.SocialUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 注册用户信息保存
     *
     * @param memberRegistVo
     */
    @Override
    public void regist(MemberRegistVo memberRegistVo) {
        MemberDao baseMapper = this.baseMapper;
        MemberEntity entity = new MemberEntity();
        //设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefualtLevel();
        entity.setLevelId(memberLevelEntity.getId());
        //检查用户名或手机号是否唯一
        checkPhoneUnique(memberRegistVo.getPhone());
        entity.setMobile(memberRegistVo.getPhone());

        checkPhoneUnique(memberRegistVo.getUserName());
        entity.setUsername(memberRegistVo.getUserName());


        String s = DigestUtils.md5Hex(memberRegistVo.getPassword());



       /* entity.setSign("" + System.currentTimeMillis());
        String apr1Crypt = Md5Crypt.apr1Crypt(memberRegistVo.getPassword(), entity.getSign());
        entity.setPassword(apr1Crypt);*/
        //spring 家提供的密码加密器
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(memberRegistVo.getPassword());
        entity.setPassword(encode);


        baseMapper.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) {
        MemberDao baseMapper = this.baseMapper;
        Integer mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0)
            throw new PhoneExistException();
    }

    @Override
    public void checkUserNameUnique(String userName) {
        MemberDao baseMapper = this.baseMapper;
        Integer username = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (username > 0) throw new UserNameExistException();
    }

    @Override
    public MemberEntity login(MemberLoginVo loginVo) {
        String loginAccount = loginVo.getLoginAccount();
        String password = loginVo.getPassword();
        //1。去数据库查询 用户或是密码查询
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginAccount).or().eq("mobile", loginAccount));
        if (entity == null) {
            //登录失败
            return null;
        } else {
            String passwordDb = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            //密码匹配
            boolean matches = passwordEncoder.matches(password, passwordDb);
            if (matches) {
                return entity;
            } else {
                return null;
            }

        }
    }

    /**
     * 对于登录和注册合并逻辑
     *
     * @param socialUser
     * @return
     */
    @Override
    public MemberEntity login(SocialUser socialUser) {
        //登录和注册的逻辑

        String uid = socialUser.getUid();

        //判断社交账户是否已经登录过

        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        if (entity != null) {
            //用户已经注册
            MemberEntity update = new MemberEntity();
            update.setId(entity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            memberDao.updateById(update);

            entity.setAccessToken(socialUser.getAccess_token());
            entity.setExpiresIn(socialUser.getExpires_in());

            return entity;
        }

        //没有查到要注册
        MemberEntity regist = new MemberEntity();
        //查询当前社交用户的社交账号信息
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("access_token", socialUser.getAccess_token());
        hashMap.put("uid", socialUser.getUid());
        try {
            HttpResponse response = HttpUtils.doGet("", "", "get", null, hashMap);
            if (response.getStatusLine().getStatusCode() == 200) {
                //查询成功
                String jsonString = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSONObject.parseObject(jsonString);

                //获取昵称
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");

                //获取性别
                regist.setNickname(name);
                regist.setGender("m".equals(gender) ? 1 : 0);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        regist.setAccessToken(socialUser.getAccess_token());
        regist.setSocialUid(socialUser.getUid());
        regist.setExpiresIn(socialUser.getExpires_in());

        baseMapper.insert(regist);

        return regist;
    }


}

