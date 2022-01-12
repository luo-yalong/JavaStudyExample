package com.lyl.jwtdemo.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.lyl.jwtdemo.common.config.JwtConfig;
import com.lyl.jwtdemo.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 罗亚龙
 * @date 2022/1/10 14:56
 */
@RestController
public class TokenController {

    @Resource
    private TokenService tokenService;
    @Autowired
    private JwtConfig jwtConfig;


    @GetMapping("/login")
    public Object login(String username,
                        String password){
        System.out.println("username = " + username + ", password = " + password);
        String userId = "123";
        String token = jwtConfig.createToken(userId);
        Map<String,Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(token)){
            map.put("token", token);
        }
        return map;
    }

    @PostMapping("/info")
    public Object info(){
        return "info";
    }

    @GetMapping("/getUserInfo")
    public Object getUserInfo(HttpServletRequest request){
        String token = jwtConfig.getUsernameFromToken(request.getHeader("token"));
        System.out.println("TokenController.getUserInfo, token = " + token);
        System.out.println("过期时间 = " + jwtConfig.getExpirationDate(token));
        return MapUtil.builder().put("username", token).build();
    }

}
