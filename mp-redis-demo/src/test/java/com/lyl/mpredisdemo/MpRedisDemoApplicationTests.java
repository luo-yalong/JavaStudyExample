package com.lyl.mpredisdemo;

import com.lyl.mpredisdemo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class MpRedisDemoApplicationTests {


    @Autowired
    private UserServiceImpl userService;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    void contextLoads() {
        userService.list().forEach(System.out::println);
    }

    @Test
    void testRedis() {
        redisTemplate.opsForValue().set("user::3", userService.getById(3));
    }
}
