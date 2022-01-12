package com.lyl.mp_multi_datasource.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lyl.mp_multi_datasource.entity.User;

import java.util.List;

/**
 * (User)表服务接口
 *
 * @author 罗亚龙
 * @since 2021-12-20 13:33:24
 */
public interface UserService extends IService<User> {

    /**
     * master 查询
     * @return
     */
    List<User> listOfMaster();

    /**
     * slave 数据库查询
     * @return
     */
    List<User> listOfSlave();
}

