package com.lyl.jwtdemo.common.config;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 罗亚龙
 * @date 2022/1/10 14:35
 * JWT配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt.config")
public class JwtConfig {

    private String secret;
    private long expire;
    private String header;

    /**
     * 创建token
     *
     * @param subject 主题
     * @return
     */
    public String createToken(String subject) {
        Date currentDate = new Date();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject(subject)
                .setIssuedAt(currentDate)
                .setExpiration(new Date(currentDate.getTime() + expire * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    /**
     * 获取token中注册信息
     *
     * @param token
     * @return
     */
    public Claims getTokenClaim(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return
     */
    public boolean isExpired(String token) {
        return getTokenClaim(token).getExpiration().before(new Date());
    }

    /**
     * 获取token失效时间
     *
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        return getTokenClaim(token).getExpiration();
    }


    public String getExpirationDate(String token) {
        Date date = getExpirationDateFromToken(token);
        return DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 获取用户名从token中
     */
    public String getUsernameFromToken(String token) {
        return getTokenClaim(token).getSubject();
    }

    /**
     * 获取jwt发布时间
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getTokenClaim(token).getIssuedAt();
    }


}
