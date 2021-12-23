package com.lyl.mybatisplus_elasticsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.mybatisplus_elasticsearch.dao.UserDao;
import com.lyl.mybatisplus_elasticsearch.entity.User;
import com.lyl.mybatisplus_elasticsearch.service.UserService;
import org.springframework.stereotype.Service;

/**
 * (User)表服务实现类
 *
 * @author 罗亚龙
 * @since 2021-12-21 16:19:34
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

}

