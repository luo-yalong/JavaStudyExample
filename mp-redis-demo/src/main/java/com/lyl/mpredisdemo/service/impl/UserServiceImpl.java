package com.lyl.mpredisdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyl.mpredisdemo.dao.UserDao;
import com.lyl.mpredisdemo.entity.User;
import com.lyl.mpredisdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * (User)表服务实现类
 *
 * @author 罗亚龙
 * @since 2022-01-11 14:19:57
 */
@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    /**
     * 用于查询操作的接口
     * @param id
     * @return
     */
    @Override
    @Cacheable(cacheNames = "user",key = "#id",sync = true)
    public User getById(Serializable id) {
        log.info("getById() called with: id = [" + id + "]");
        return super.getById(id);
    }

    /**
     * 用于删除操作的接口
     * @param id
     * @return
     */
    @Override
    @CacheEvict(cacheNames = "user",key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CachePut(cacheNames = "user",key = "#user.id")
    public User update(User user) {
        super.saveOrUpdate(user);
        return super.getById(user.getId());
    }

    @Override
    @CachePut(cacheNames = "user",key = "#user.id")
    public User insert(User user) {
        super.saveOrUpdate(user);
        return super.getById(user.getId());
    }
}

