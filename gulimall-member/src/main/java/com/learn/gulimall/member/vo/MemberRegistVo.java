package com.learn.gulimall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * packageName = com.learn.gulimall.member.vo
 * author = Casey
 * Data = 2020/4/29 6:20 下午
 **/

@Data
public class MemberRegistVo {
    private String userName;
    private String password;
    private String phone;
}
