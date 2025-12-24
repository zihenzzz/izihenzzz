package com.railbooking.dto;

import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String phone;
    private String realName;
    private String idCard;
}
