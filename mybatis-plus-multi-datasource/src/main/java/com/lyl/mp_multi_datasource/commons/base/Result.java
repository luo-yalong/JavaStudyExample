package com.lyl.mp_multi_datasource.commons.base;

import cn.hutool.core.date.DateUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 罗亚龙
 * @date 2021/12/20 13:42
 */
@Data
@Accessors(chain = true)
public class Result<T> {

    /**
     * 结果状态，具体状态码
     */
    private Integer code;

    /**
     * 本次接口调用的结果描述
     */
    private String msg;
    /**
     * 返回的数据
     */
    private T data;
    /**
     * 接口调用时间
     */
    private String timestamp;

    public Result() {
        this.timestamp = DateUtil.now();
    }

    /**
     * 成功方法
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data) {
        Result<T> resultData = new Result<>();
        resultData.setCode(ReturnCode.SUCCESS.getCode());
        resultData.setMsg(ReturnCode.SUCCESS.getMsg());
        resultData.setData(data);
        return resultData;
    }


    /**
     * 失败方法
     *
     * @param code 状态码
     * @param msg  错误消息
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> resultData = new Result<>();
        resultData.setCode(code);
        resultData.setMsg(msg);
        return resultData;
    }

    /**
     * 失败方法
     *
     * @param returnCode 状态码-错误信息
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(ReturnCode returnCode) {
        Result<T> resultData = new Result<>();
        resultData.setCode(returnCode.getCode());
        resultData.setMsg(returnCode.getMsg());
        return resultData;
    }

}
