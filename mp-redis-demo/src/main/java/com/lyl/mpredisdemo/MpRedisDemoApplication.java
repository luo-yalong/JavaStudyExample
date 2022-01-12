package com.lyl.mpredisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author luoyalong
 */
@SpringBootApplication
@EnableCaching
public class MpRedisDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MpRedisDemoApplication.class, args);
	}

}
