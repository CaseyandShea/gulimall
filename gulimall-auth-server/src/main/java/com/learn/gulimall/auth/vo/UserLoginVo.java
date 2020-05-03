package com.learn.gulimall.auth.vo;

import lombok.Data;

/**
 * packageName = com.learn.gulimall.auth.vo
 * author = Casey
 * Data = 2020/5/1 10:59 上午
 **/

@Data
public class UserLoginVo {
    private String loginAccount;
    private String  password;
}
