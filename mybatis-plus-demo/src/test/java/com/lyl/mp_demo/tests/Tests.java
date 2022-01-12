package com.lyl.mp_demo.tests;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.lyl.mp_demo.entity.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 罗亚龙
 * @date 2022/1/8 15:54
 */
public class Tests {

    @Test
    void testFilterChinese() {
        String regex = "[(\\u4e00-\\u9fa5)| |.|Lcci]";
        String str = "面试题 01.01removeDuplicateNodeLcci";
        str = str.replaceAll(regex, "");
        System.out.println("[" + str + "]");
    }

    @Test
    void testRandomData() {
        Faker faker = new Faker();
        Name name = faker.name();
        System.out.println("name.firstName() = " + name.firstName());
    }

    @Test
    void testBatchInsertUser() {
        int num = 100;
        Faker faker = new Faker();
        Name name;
        List<User> users = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            name = faker.name();
            User user = new User()
                    .setName(name.firstName())
                    .setAge(faker.random().nextInt(18, 50))
                    .setCreateTime(faker.date().birthday().toString())
                    .setSex(faker.random().nextInt(1));

            users.add(user);
            System.out.println(user);
        }
    }
}
