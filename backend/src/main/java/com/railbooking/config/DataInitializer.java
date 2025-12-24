package com.railbooking.config;

import com.railbooking.entity.User;
import com.railbooking.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 - 确保测试用户存在
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 检查并创建管理员用户
        initUser("admin", "admin123", "ADMIN", "管理员", "admin@railbooking.com");
        // 检查并创建测试用户
        initUser("test", "test123", "USER", "测试用户", "test@railbooking.com");
        
        log.info("数据初始化完成");
    }

    private void initUser(String username, String password, String role, String realName, String email) {
        User existingUser = userMapper.selectByUsername(username);
        if (existingUser == null) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setRealName(realName);
            user.setEmail(email);
            user.setPhone("13800000000");
            user.setIdCard("110101199001011234");
            userMapper.insert(user);
            log.info("创建用户: {} ({})", username, role);
        } else {
            // 更新密码为BCrypt格式
            String encodedPassword = passwordEncoder.encode(password);
            existingUser.setPassword(encodedPassword);
            userMapper.updateById(existingUser);
            log.info("更新用户密码: {}", username);
        }
    }
}
