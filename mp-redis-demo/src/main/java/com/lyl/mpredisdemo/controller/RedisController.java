package com.lyl.mpredisdemo.controller;

import com.lyl.mpredisdemo.entity.User;
import com.lyl.mpredisdemo.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 罗亚龙
 * @date 2022/1/11 14:16
 */
@RestController
public class RedisController {


    @Resource
    private UserService userService;

    @GetMapping("/user/{id:\\d+}")
    public User getById(@PathVariable("id") long id) {
        return userService.getById(id);
    }

    @DeleteMapping("/user/{id:\\d+}")
    public boolean removeById(@PathVariable("id") long id) {
        return userService.removeById(id);
    }

    @PutMapping("/user")
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    @PostMapping("/user")
    public User insert(@RequestBody User user){
        return userService.insert(user);
    }


}
