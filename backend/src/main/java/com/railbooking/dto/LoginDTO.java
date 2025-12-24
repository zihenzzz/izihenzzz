package com.railbooking.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 登录请求DTO
 */
@Data
public class LoginDTO {
    private String username;
    private String password;
}
