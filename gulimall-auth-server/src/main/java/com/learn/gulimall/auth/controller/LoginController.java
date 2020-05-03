package com.learn.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.google.errorprone.annotations.RestrictedApi;
import com.learn.gulimall.auth.feign.MemberFeignService;
import com.learn.gulimall.auth.feign.ThirdPartFeignService;
import com.learn.gulimall.auth.vo.UserLoginVo;
import com.learn.gulimall.auth.vo.UserRegisterVo;
import com.learn.gulimall.common.constant.AuthConstant;
import com.learn.gulimall.common.exception.BizCodeEnum;
import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.common.vo.MemberRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * packageName = com.learn.gulimall.auth.controller
 * author = Casey
 * Data = 2020/4/29 3:30 下午
 **/

@Controller
public class LoginController {
    /* */
    /**
     * 发送请求直接跳转我们
     * Spring MVC viewController 将请求和页面映射过来
     *
     * @return
     *//*

    @GetMapping("/login.html")
    public String loginPage(){

        return "login";
    }

    @GetMapping("/register.html")
    public String registerPage(){
        return "register";
    }*/

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 发动验证码
     *
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        //接口防刷 看是否有发送了验证码
        String s = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(s)) {
            long cacheTime = Long.parseLong(s.split("_")[1]);
            if (System.currentTimeMillis() - cacheTime < 60000) {
                return R.error(BizCodeEnum.AUTH_TOO_FAST_EXCEPTION.getCode(), BizCodeEnum.AUTH_TOO_FAST_EXCEPTION.getMsg());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 5);
        //为了防止一直不停的发送验证码  存验证码时候存上发送时间
        String redisCode = code + "_" + System.currentTimeMillis();
        //2. 需要二次校验所以需要存起来   因为有过期时间 所以存在redis里刚好  设置好过期时间
        stringRedisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX + phone, redisCode, 10, TimeUnit.MINUTES);
        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }

    @PostMapping("/regist")
    public String regist(@Valid UserRegisterVo vo, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            /**
             * 简写fieldError -> {
             *                 return fieldError.getDefaultMessage();
             *             }
             *  为：FieldError::getDefaultMessage
             */
            Map<String, String> collect = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            model.addAttribute("errors", collect);
            redirectAttributes.addAllAttributes(collect);
            // 校验出错转发
            return "redirect:/http:auth.gulimall.com/reg.html";
        }
        //1.校验验证码
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equalsIgnoreCase(s.split("_")[0])) {
                //删除验证码  ；令牌机制
                stringRedisTemplate.delete(AuthConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                R r = memberFeignService.register(vo);
                if (r.getCode() == 0) {
                    //登录成功
                    return "redirect:/http:auth.gulimall.com/login.html";
                } else {
                    Map<String, String> error = new HashMap<>();
                    error.put("msg", r.getData("msg", new TypeReference<String>() {
                    }));
                    redirectAttributes.addAllAttributes(error);

                    return "redirect:/http:auth.gulimall.com/reg.html";
                }
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("code", "验证码错误");
                redirectAttributes.addAllAttributes(error);
                return "redirect:/http:auth.gulimall.com/reg.html";
            }
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("code", "验证码错误");
            redirectAttributes.addAllAttributes(error);
            return "redirect:/http:auth.gulimall.com/reg.html";
        }

    }


    @PostMapping("/login")
    @ResponseBody
    public R login(@RequestBody UserLoginVo loginVo , HttpSession session) {
        R login = memberFeignService.login(loginVo);
        String errorMsg = "";
        if (login.getCode() == 0) {
            //登录成功放到session中
            MemberRespVo data = login.getData(new TypeReference<MemberRespVo>() {
            });
            session.setAttribute("loginUser",data);
            //成功
            return R.ok();
        } else {
            //失败

            errorMsg = login.getData("msg",
                    new TypeReference<String>() {
                    }
            );

        }
        return R.error(errorMsg);
    }


    @GetMapping("login.html")
    public  String loginPage(HttpSession session){
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser != null){
            //没有登录
            return "redicect:http://gulimall.com";
        }

        return "login";
    }
}
