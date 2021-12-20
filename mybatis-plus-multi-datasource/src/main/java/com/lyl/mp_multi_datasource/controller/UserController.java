package com.lyl.mp_multi_datasource.controller;


import com.lyl.mp_multi_datasource.commons.base.BaseController;
import com.lyl.mp_multi_datasource.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author 罗亚龙
 * @date 2021/12/20 13:37
 */
@Slf4j
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

    /**
     * 查询数据
     *
     * @param type 类型：0：master数据库   其他：slave数据库
     * @return 用户列表
     */
    @GetMapping("/list/{type:\\d*}")
    public List<User> getUserList(@PathVariable("type")int type) {
        System.out.println("type = " + type);
        //如果type = 0 从master库中查询，否则从slave中查询数据
        if (type == 0) {
            log.info("数据源 -> master");
            return userService.listOfMaster();
        } else {
            log.info("数据源 -> slave");
            return userService.listOfSlave();
        }
    }

}
