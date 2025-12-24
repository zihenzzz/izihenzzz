package com.railbooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.railbooking.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 注册新用户
     */
    boolean register(User user);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
}
