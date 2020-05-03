package com.learn.gulimall.member.controller;

import com.learn.gulimall.common.exception.BizCodeEnum;
import com.learn.gulimall.common.utils.PageUtils;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.member.entity.MemberEntity;
import com.learn.gulimall.member.exception.PhoneExistException;
import com.learn.gulimall.member.exception.UserNameExistException;
import com.learn.gulimall.member.service.MemberService;
import com.learn.gulimall.member.vo.MemberLoginVo;
import com.learn.gulimall.member.vo.MemberRegistVo;
import com.learn.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author Casey
 * @email Casey_Address@163.com
 * @date 2020-04-13 13:58:56
 */
@RestController
@RequestMapping("member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    @PostMapping("/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser) {
        MemberEntity entity = memberService.login(socialUser);
        if (entity == null) {
            return R.error();
        } else {
            //登录成功
            R ok = R.ok();
            ok.setData(entity);
            return ok ;
        }

    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo loginVo) {
        MemberEntity entity = memberService.login(loginVo);
        if (entity == null) {
            return R.error(BizCodeEnum.ACCOUT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.ACCOUT_PASSWORD_EXCEPTION.getMsg());
        }
        R r = R.ok();
        r.setData(entity);
        return r;
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberRegistVo memberRegistVo) {
        try {
            memberService.regist(memberRegistVo);
        } catch (PhoneExistException e) {
            R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UserNameExistException e) {
            R.error(BizCodeEnum.MEMBER_EXIST_EXCEPTION.getCode(), BizCodeEnum.MEMBER_EXIST_EXCEPTION.getMsg());

        }


        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
