package com.lyl.mp_multi_datasource.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.mp_multi_datasource.dao.UserDao;
import com.lyl.mp_multi_datasource.entity.User;
import com.lyl.mp_multi_datasource.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (User)表服务实现类
 *
 * @author 罗亚龙
 * @since 2021-12-20 13:33:24
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    /**
     * master 查询
     * @return
     */
    @Override
    public List<User> listOfMaster(){
        return this.list(Wrappers.emptyWrapper());
    }

    /**
     * slave 数据库查询
     * @return
     */
    @DS("slave")
    @Override
    public List<User> listOfSlave(){
        return this.list(Wrappers.emptyWrapper());
    }
}

