package com.lyl.mpredisdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyl.mpredisdemo.entity.User;

/**
 * (User)表服务接口
 *
 * @author 罗亚龙
 * @since 2022-01-11 14:19:57
 */
public interface UserService extends IService<User> {

    /**
     * 添加或者更新
     * @param user 用户
     * @return
     */
    public User update(User user);

    /**
     * 添加
     * @param user
     * @return
     */
    User insert(User user);
}

