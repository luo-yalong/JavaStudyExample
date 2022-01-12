package com.lyl.mp_demo.tests;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

/**
 * @author 罗亚龙
 * @date 2022/1/10 13:52
 */
public class TestJWT {

    private final int EXPIRE_TIME = 24 * 60 * 60 * 60;
    private final String signature = "admin";

    @Test
    void jwtEncode() {
        JwtBuilder builder = Jwts.builder();
        String jwtToken = builder
                //header
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //payload
                .claim("username", "tom")
                .claim("role", "admin")
                .setSubject("admin-jwt-test")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .setId(UUID.randomUUID().toString())
                //签名
                .signWith(SignatureAlgorithm.HS256, signature)
                .compact();

        System.out.println("jwtToken = " + jwtToken);
    }

    @Test
    void jwtDecode() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InRvbSIsInJvbGUiOiJhZG1pbiIsInN1YiI6ImFkbWluLWp3dC10ZXN0IiwiZXhwIjoxNjQxODAwMjEzLCJqdGkiOiIxN2I0MzQ1My0xMzEzLTRjYTktODM1ZS1hNDcwMWMzMTVmNGEifQ.YQzD_b6IS_vyRLYJZrLer5KehbXPSdt1wyKY2fb0UPg";
        JwtParser jwtParser = Jwts.parser();
        Jws<Claims> claimsJws = jwtParser.setSigningKey(signature)
                .parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        System.out.println("body.get(\"username\") = " + body.get("username"));
        System.out.println("body.get(\"role\") = " + body.get("role"));
        System.out.println("body.getId() = " + body.getId());
        System.out.println("body.getSubject() = " + body.getSubject());
        System.out.println("body.getExpiration() = " + body.getExpiration());

    }
}
