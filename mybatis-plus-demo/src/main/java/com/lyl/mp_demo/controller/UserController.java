package com.lyl.mp_demo.controller;

import com.lyl.mp_demo.commons.base.BaseController;
import com.lyl.mp_demo.entity.User;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * @author 罗亚龙
 * @date 2021/12/20 13:37
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    /**
     * 查看用户表数据
     *
     * @param id id
     * @return 用户信息
     */
    @GetMapping("{id:\\d*}")
    public User getUser(@PathVariable("id") Serializable id) {
        return userService.getById(id);
    }

    /**
     * 添加数据
     *
     * @param user
     */
    @PostMapping
    public User addUser(@RequestBody User user) {
        userService.save(user);
        return userService.getById(user.getId());
    }

    /**
     * 更新或者保存用户信息
     *
     * @param user 用户表
     * @return 用户信息
     */
    @PutMapping
    public User updateUser(@RequestBody User user) {
        userService.saveOrUpdate(user);
        return userService.getById(user.getId());
    }

    /**
     * 通过id删除用户
     *
     * @param id id
     */
    @DeleteMapping("{id:\\d*}")
    public void deleteUser(@PathVariable Serializable id) {
        userService.removeById(id);
    }
}
