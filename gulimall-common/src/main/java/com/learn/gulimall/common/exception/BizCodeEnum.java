package com.learn.gulimall.common.exception;

/**
 * packageName = com.learn.gulimall.common.exception
 * author = Casey
 * Data = 2020/4/15 4:30 下午
 **/
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验异常"),
    PRODUCT_UP_EXCEPTION(20001,"商品上架错误");
    private int code;
    private String msg;
    BizCodeEnum(int code,String msg){
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

