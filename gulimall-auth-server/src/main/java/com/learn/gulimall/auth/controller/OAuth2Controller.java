package com.learn.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.learn.gulimall.auth.feign.MemberFeignService;
import com.learn.gulimall.common.vo.MemberRespVo;
import com.learn.gulimall.auth.vo.WeiboUser;
import com.learn.gulimall.common.utils.HttpUtils;
import com.learn.gulimall.common.utils.R;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName = com.learn.gulimall.auth.controller
 * author = Casey
 * Data = 2020/5/1 12:37 下午
 **/

/**
 * 社交登录处理
 */
@Controller
public class OAuth2Controller {
    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        //换取微博的Acces——token
        Map<String ,String> map = new  HashMap<>();
        map.put("code",code);
        map.put("client_id","1229954378");
        map.put("client_secret","52600730d19e450c1523003127c5719a");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://caseymall.com/oauth2.0/weibo/success");

        HttpResponse response = HttpUtils.doPost("api.weibo.com", "/oatuh2/access_token", "post", null, null, map);

        //查看是否换取成功
        if (response.getStatusLine().getStatusCode() == 200){
            String jsonString = EntityUtils.toString(response.getEntity());
            WeiboUser weiboUser = JSON.parseObject(jsonString, WeiboUser.class);

            //知道了社交登录
            //1。第一次登录，就自动注册（为当前社交用户生成一个会员信息账号 只要是这个账号就对应指定的会员）
            //登录或是注册社交登录
            R r = memberFeignService.oauthLogin(weiboUser);
          if ( r.getCode()  == 0){
              MemberRespVo respVo =  r.getData(new TypeReference<MemberRespVo>(){});
              session.setAttribute("loginuser",respVo);
              return  "http://caseymall.com/oauth2.0/weibo/success";
          }

        }else {
            return "redirect:http://caseymall.com/login.html";
        }
        //跳回登录首页
        return "http://caseymall.com/oauth2.0/weibo/success";
    }

}
