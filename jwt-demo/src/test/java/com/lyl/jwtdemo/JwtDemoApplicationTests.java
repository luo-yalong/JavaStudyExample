package com.lyl.jwtdemo;

import cn.hutool.core.date.DateUtil;
import com.lyl.jwtdemo.common.config.JwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class JwtDemoApplicationTests {

	@Autowired
	private JwtConfig jwtConfig;

	@Test
	void contextLoads() {
		String token = jwtConfig.createToken("张三");
		System.out.println("token = " + token);

		Date expirationDateFromToken = jwtConfig.getExpirationDateFromToken(token);
		String data = DateUtil.format(expirationDateFromToken, "yyyy-MM-dd HH:mm:ss");
		System.out.println("过期时间 = " + data);

		String username = jwtConfig.getUsernameFromToken(token);
		System.out.println("username = " + username);

		boolean expired = jwtConfig.isExpired(token);
		System.out.println("expired = " + expired);
	}

}
