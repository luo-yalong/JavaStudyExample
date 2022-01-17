package com.lyl.ms;

import com.lyl.ms.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class MiaoshaApplicationTests {


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserDao userDao;

    @Test
    void testUserDao() {
        System.out.println("userDao.selectById(1001) = " + userDao.selectById(1001));
        userDao.selectList(null)
                .forEach(System.out::println);
    }

    @Test
    void contextLoads() {

        redisTemplate.opsForValue().set("stock::1","123");

        System.out.println("stringRedisTemplate.hasKey(\"stock::1\") = " + redisTemplate.hasKey("stock::1"));

        System.out.println("stringRedisTemplate.opsForValue().get(\"stock::1\") = " + redisTemplate.opsForValue().get("stock::1"));
    }

}
