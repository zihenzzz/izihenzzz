package com.railbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * 用户实体类
 */
@Data
@TableName("users")
public class User implements UserDetails {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 角色：USER-普通用户，ADMIN-管理员
     */
    private String role;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    // UserDetails 接口实现
    @Override
    @TableField(exist = false)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    @TableField(exist = false)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @TableField(exist = false)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @TableField(exist = false)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @TableField(exist = false)
    public boolean isEnabled() {
        return deleted == null || deleted == 0;
    }
}
