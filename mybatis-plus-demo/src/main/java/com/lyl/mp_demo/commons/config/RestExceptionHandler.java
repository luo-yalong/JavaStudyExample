package com.lyl.mp_demo.commons.config;

import com.lyl.mp_demo.commons.base.Result;
import com.lyl.mp_demo.commons.base.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 罗亚龙
 * @date 2021/12/20 14:13
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * 默认全局异常处理
     *
     * @param e 异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<String> onException(Exception e) {
        log.error("全局异常信息", e);
        return Result.fail(ReturnCode.FAIL);
    }
}
