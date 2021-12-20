package com.lyl.mp_multi_datasource.commons.base;

import com.lyl.mp_multi_datasource.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 罗亚龙
 * @date 2021/12/20 13:38
 */
@Component
public class BaseController {

    @Resource
    public HttpServletRequest request;

    /**
     * 用户表service
     */
    @Resource
    public UserService userService;
}
