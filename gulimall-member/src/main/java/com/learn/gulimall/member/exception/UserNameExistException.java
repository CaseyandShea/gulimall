package com.learn.gulimall.member.exception;

/**
 * packageName = com.learn.gulimall.member.exception
 * author = Casey
 * Data = 2020/4/29 6:36 下午
 **/
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名已经存在");
    }
}
