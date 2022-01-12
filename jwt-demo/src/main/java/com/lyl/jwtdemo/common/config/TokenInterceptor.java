package com.lyl.jwtdemo.common.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 罗亚龙
 * @date 2022/1/10 14:57
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Resource
    private JwtConfig jwtConfig;
    public static final String LOGIN_PAGE_URI = "/login";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //地址过滤
        String uri = request.getRequestURI();
        if (uri.contains(LOGIN_PAGE_URI)){
            return true;
        }

        //token验证
        String token = request.getHeader(jwtConfig.getHeader());
        if (StrUtil.isBlank(token)){
            throw new IllegalArgumentException(jwtConfig.getHeader() + "不能为空");
        }
        if (jwtConfig.isExpired(token)) {
            throw new AuthException(jwtConfig.getHeader() + "已过期,请重新登登录");
        }
        System.out.println("进行验证 -> 过期时间：" + jwtConfig.getExpirationDate(token));
        //设置 identityId 用户身份ID
        request.setAttribute("identityId", jwtConfig.getUsernameFromToken(token));
        return true;
    }
}
