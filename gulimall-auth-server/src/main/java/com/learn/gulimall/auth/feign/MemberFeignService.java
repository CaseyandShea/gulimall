package com.learn.gulimall.auth.feign;

import com.learn.gulimall.auth.vo.UserLoginVo;
import com.learn.gulimall.auth.vo.UserRegisterVo;
import com.learn.gulimall.auth.vo.WeiboUser;
import com.learn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * packageName = com.learn.gulimall.auth.feign
 * author = Casey
 * Data = 2020/4/29 7:13 下午
 **/

@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/register")
    R register(@RequestBody UserRegisterVo memberRegistVo);

    //远程登录功能
    @PostMapping("/member /login")
    R login(@RequestBody UserLoginVo loginVo);

    @PostMapping("/member/oauth2/login")
    R oauthLogin(@RequestBody WeiboUser socialUser);
}
