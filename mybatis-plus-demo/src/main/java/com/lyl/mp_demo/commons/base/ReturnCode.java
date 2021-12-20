package com.lyl.mp_demo.commons.base;

/**
 * @author 罗亚龙
 * @date 2021/12/20 13:56
 */
public enum ReturnCode {

    //操作成功
    SUCCESS(100, "操作成功"),
    // 操作失败
    FAIL(500, "操作失败"),
    //参数异常
    PARAM_ERR(301, "参数异常");

    /**
     * code
     */
    private int code;

    /**
     * 消息提示
     */
    private String msg;

    ReturnCode(int code, String msg) {
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
