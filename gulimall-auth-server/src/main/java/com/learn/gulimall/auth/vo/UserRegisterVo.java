package com.learn.gulimall.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * packageName = com.learn.gulimall.auth.vo
 * author = Casey
 * Data = 2020/4/29 5:28 下午
 **/

@Data
public class UserRegisterVo
{

    private String userName;
    @NotNull
    private String password;
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phone;
    private String code;
}
