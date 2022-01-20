package com.lyl.ms.common.config;

import com.lyl.ms.common.exeception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:44
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionConfig {

    /**
     * 处理自定义异常
     * @param e 自定义异常
     * @return msg
     */
    @ExceptionHandler(MyException.class)
    public String onMyException(MyException e) {
        log.error("自定义异常 : [{}]", e.getMsg());
        return e.getMsg();
    }

    /**
     * 处理其他异常
     * @param e 异常
     * @return msg
     */
    @ExceptionHandler(Exception.class)
    public String onException(Exception e){
        log.error("全局异常捕获 : [{}]", e.getMessage());
        return e.getMessage();
    }
}
