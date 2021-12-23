package com.lyl.mybatisplus_elasticsearch;

import com.lyl.mybatisplus_elasticsearch.entity.NbaPlayer;
import com.lyl.mybatisplus_elasticsearch.entity.User;
import com.lyl.mybatisplus_elasticsearch.service.NbaPlayerService;
import com.lyl.mybatisplus_elasticsearch.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MybatisPlusElasticsearchApplicationTests {

    @Autowired
    private NbaPlayerService service;

    @Test
    void contextLoads() {

        NbaPlayer nbaPlayer = new NbaPlayer()
                .setId(999)
                .setDisplayName("罗亚龙");
        service.addPlayer(nbaPlayer, "999");
    }

    @Test
    void testQueryAll() {
        service.list()
                .forEach(System.out::println);
    }

    @Autowired
    private UserService userService;

    @Test
    void testGetPlayer() {
        userService.list()
                .forEach(System.out::println);
    }

    @Test
    void testInsert() {
        User user = new User();
        user.setCustomerId(7);
        user.setName("王五")
                .setSex(0);
        userService.save(user);
    }

}
