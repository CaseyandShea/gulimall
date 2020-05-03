package com.learn.gulimall.member.exception;

/**
 * packageName = com.learn.gulimall.member.exception
 * author = Casey
 * Data = 2020/4/29 6:37 下午
 **/
public class PhoneExistException extends RuntimeException {
    public PhoneExistException(){
        super("该手机号已经注册");
    }

}
