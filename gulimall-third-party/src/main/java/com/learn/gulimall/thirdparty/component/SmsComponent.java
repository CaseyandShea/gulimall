package com.learn.gulimall.thirdparty.component;

import com.learn.gulimall.thirdparty.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName = com.learn.gulimall.thirdparty.component
 * author = Casey
 * Data = 2020/4/29 4:28 下午
 **/
@Component
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
public class SmsComponent {
    private String host;
    private String path;
    private String appCode;

    public void sendSmsCode(String phone, String code) {

        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appCode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("code", code);
        querys.put("phone", phone);
        //模版格式
        querys.put("skin", "1");
        //模版签名（可以找客服生成自己的模版签名）
        querys.put("sign", "175622");
        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/Tools.zip

        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
