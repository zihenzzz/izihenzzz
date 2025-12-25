package com.railbooking.controller;

import com.railbooking.service.TrainService;
import com.railbooking.util.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 用于开发和测试
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final TrainService trainService;

    public TestController(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public ResponseEntity<Result<Map<String, Object>>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("system", "汽车票预订系统");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        info.put("status", "运行正常");
        
        return ResponseEntity.ok(Result.success(info));
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Result<Map<String, String>>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(Result.success(health));
    }
}
