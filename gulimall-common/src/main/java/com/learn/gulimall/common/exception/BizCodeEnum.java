package com.learn.gulimall.common.exception;

/**
 * packageName = com.learn.gulimall.common.exception
 * author = Casey
 * Data = 2020/4/15 4:30 下午
 * 11
 **/
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验异常"),
    PRODUCT_UP_EXCEPTION(20001, "商品上架错误"),
    MEMBER_EXIST_EXCEPTION(40001, "用户名已经存在"),
    PHONE_EXIST_EXCEPTION(40002, "手机号已经注册"),
    ACCOUT_PASSWORD_EXCEPTION(40003, "账号或密码错误"),
    AUTH_TOO_FAST_EXCEPTION(30001, "短信发送太频繁，请稍后在发送验证码");
    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

