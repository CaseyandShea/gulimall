package com.learn.gulimall.member.vo;

import lombok.Data;

/**
 * packageName = com.learn.gulimall.member.vo
 * author = Casey
 * Data = 2020/5/1 1:10 下午
 **/

@Data
public class SocialUser {
    private String uid;
    private String access_token;
    private String expires_in;
}
