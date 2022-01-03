package com.lyl.mp_demo;

import cn.hutool.core.date.DateUtil;
import com.lyl.mp_demo.dao.UserDao;
import com.lyl.mp_demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MybatisPlusDemoApplicationTests {

    @Autowired
    private UserDao userDao;

    @Test
    void test1() {
        System.out.println("测试乱码");
    }

    @Test
    void testQueryAll(){
        userDao.selectList(null)
                .forEach(System.out::println);
    }

    @Test
    void testDeleteById(){
        int i = userDao.deleteById(6);
        System.out.println("i = " + i);
    }

    @Test
    void contextLoads() {
        User user = new User();
        user.setName("jack")
                .setId(7)
                .setSex(1)
                .setAge(29);
        int i = userDao.updateById(user);
        System.out.println("i = " + i);
    }

    @Test
    void testException() {

    }

    @Test
    void testOptimismVersion(){
        User user = userDao.selectById(7);
        user.setName("jack--abc");
        userDao.updateById(user);
    }

    @Test
    void test2(){
        System.out.println("LocalDateTime.now() = " + DateUtil.now());
    }

}
