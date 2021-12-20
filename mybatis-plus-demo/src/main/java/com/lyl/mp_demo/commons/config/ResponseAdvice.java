package com.lyl.mp_demo.commons.config;

import com.alibaba.fastjson.JSON;
import com.lyl.mp_demo.commons.base.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;

/**
 * @author 罗亚龙
 * @date 2021/12/20 14:08
 */
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 是否支持 advice
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 对返回的数据进行处理
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) {
            return JSON.toJSONString(Result.success(body));
        }
        if (body instanceof Result) {
            return body;
        }
        if (body instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap) body;
            return Result.fail((int) map.get("status"), (String) map.get("error"));
        }
        return Result.success(body);
    }
}
