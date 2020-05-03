package com.learn.gulimall.auth.feign;

import com.learn.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * packageName = com.learn.gulimall.auth.feign
 * author = Casey
 * Data = 2020/4/29 4:55 下午
 **/

@FeignClient("gulimall-third-party")
public interface ThirdPartFeignService {
    /**
     * 调用第三方服务的调用接口
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
