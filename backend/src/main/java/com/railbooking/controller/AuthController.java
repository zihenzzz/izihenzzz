package com.railbooking.controller;

import com.railbooking.entity.User;
import com.railbooking.security.JwtTokenProvider;
import com.railbooking.service.UserService;
import com.railbooking.util.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                         UserService userService,
                         JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, String>>> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("tokenType", "Bearer");

            return ResponseEntity.ok(Result.success("登录成功", response));
        } catch (AuthenticationException e) {
            log.warn("登录失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error("用户名或密码错误"));
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Result<Void>> register(@Valid @RequestBody RegisterRequest request) {
        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(Result.error("两次输入的密码不一致"));
        }

        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Result.error("用户名已存在"));
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRealName(request.getRealName());
        user.setIdCard(request.getIdCard());

        boolean success = userService.register(user);
        if (success) {
            return ResponseEntity.ok(Result.success("注册成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(Result.error("注册失败"));
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Result<User>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        if (user != null) {
            // 清除敏感信息
            user.setPassword(null);
            return ResponseEntity.ok(Result.success(user));
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Result.success("登出成功", null));
    }

    /**
     * 登录请求对象
     */
    @lombok.Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * 注册请求对象
     */
    @lombok.Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private String confirmPassword;
        private String email;
        private String phone;
        private String realName;
        private String idCard;
    }
}
