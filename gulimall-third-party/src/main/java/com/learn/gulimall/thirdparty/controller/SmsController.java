package com.learn.gulimall.thirdparty.controller;

import com.learn.gulimall.common.utils.R;
import com.learn.gulimall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName = com.learn.gulimall.thirdparty.controller
 * author = Casey
 * Data = 2020/4/29 4:48 下午
 **/

@RestController
@Controller
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    SmsComponent smsComponent;
    /**
     * 提供给别的服务调用
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }
}
