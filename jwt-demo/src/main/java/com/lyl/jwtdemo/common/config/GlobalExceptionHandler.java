package com.lyl.jwtdemo.common.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.message.AuthException;

/**
 * @author 罗亚龙
 * @date 2022/1/10 15:14
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {AuthException.class})
    public Object authException(Exception e) {
        e.printStackTrace();
        return e.getMessage();
    }

    @ExceptionHandler(value = {Exception.class})
    public String onException(Exception e) {
        e.printStackTrace();
        return e.getMessage();
    }

}
