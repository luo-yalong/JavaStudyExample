package com.lyl.mp_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.mp_demo.dao.UserDao;
import com.lyl.mp_demo.entity.User;
import com.lyl.mp_demo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * (User)表服务实现类
 *
 * @author 罗亚龙
 * @since 2021-12-20 13:33:24
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

}

