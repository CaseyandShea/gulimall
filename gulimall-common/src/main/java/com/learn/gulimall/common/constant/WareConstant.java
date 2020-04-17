package com.learn.gulimall.common.constant;

/**
 * packageName = com.learn.gulimall.common.constant
 * author = Casey
 * Data = 2020/4/17 3:18 下午
 **/
public class WareConstant {
    public enum PurchaseStateEnum{
        CREATED(0, "新建"), ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"), FINSH(3, "已完成"),
        HAS_ERROR(4, "有异常");
        private int code;
        private String msg;

        PurchaseStateEnum(int code, String msg) {
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

    public enum PurchaseDetialStateEnum{
        CREATED(0, "新建"), ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"), FINSH(3, "已完成"),
        HAS_ERROR(4, "有异常");
        private int code;
        private String msg;

        PurchaseDetialStateEnum(int code, String msg) {
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
}
