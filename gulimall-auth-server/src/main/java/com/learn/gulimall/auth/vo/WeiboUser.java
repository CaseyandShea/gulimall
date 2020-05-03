package com.learn.gulimall.auth.vo;

import lombok.Data;

/**
 * packageName = com.learn.gulimall.auth.vo
 * author = Casey
 * Data = 2020/5/1 12:58 下午
 **/
@Data
public class WeiboUser {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
