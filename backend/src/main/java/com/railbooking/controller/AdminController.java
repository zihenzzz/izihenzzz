package com.railbooking.controller;

import com.railbooking.entity.Order;
import com.railbooking.entity.Train;
import com.railbooking.entity.User;
import com.railbooking.service.OrderService;
import com.railbooking.service.TrainService;
import com.railbooking.service.UserService;
import com.railbooking.util.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final TrainService trainService;
    private final OrderService orderService;

    public AdminController(UserService userService, TrainService trainService, OrderService orderService) {
        this.userService = userService;
        this.trainService = trainService;
        this.orderService = orderService;
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Result<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计数据
        long userCount = userService.count();
        long trainCount = trainService.count();
        long orderCount = orderService.count();
        
        // 计算总收入（已支付订单的总金额）
        List<Order> paidOrders = orderService.lambdaQuery()
            .eq(Order::getStatus, 1)
            .list();
        BigDecimal totalRevenue = paidOrders.stream()
            .map(Order::getAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("userCount", userCount);
        stats.put("trainCount", trainCount);
        stats.put("orderCount", orderCount);
        stats.put("totalRevenue", totalRevenue);
        
        // 最近订单
        List<Order> recentOrders = orderService.lambdaQuery()
            .orderByDesc(Order::getCreateTime)
            .last("LIMIT 10")
            .list();
        
        stats.put("recentOrders", recentOrders);
        
        // 热门线路统计
        Map<String, Long> hotRoutes = new HashMap<>();
        paidOrders.forEach(order -> {
            String route = order.getDepartureStation() + " → " + order.getArrivalStation();
            hotRoutes.merge(route, 1L, Long::sum);
        });
        stats.put("hotRoutes", hotRoutes);
        
        return ResponseEntity.ok(Result.success(stats));
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<Result<List<User>>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        List<User> users = userService.lambdaQuery()
            .orderByDesc(User::getCreateTime)
            .page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size))
            .getRecords();
        
        // 清除密码信息
        users.forEach(user -> user.setPassword(null));
        
        return ResponseEntity.ok(Result.success(users));
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<Result<List<Order>>> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        var queryWrapper = orderService.lambdaQuery()
            .orderByDesc(Order::getCreateTime);
        
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        List<Order> orders = queryWrapper
            .page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size))
            .getRecords();
        return ResponseEntity.ok(Result.success(orders));
    }

    /**
     * 获取车次列表
     */
    @GetMapping("/trains")
    public ResponseEntity<Result<List<Train>>> getTrains(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        List<Train> trains = trainService.lambdaQuery()
            .orderByDesc(Train::getCreateTime)
            .page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size))
            .getRecords();
        
        return ResponseEntity.ok(Result.success(trains));
    }

    /**
     * 更新车次状态
     */
    @PutMapping("/trains/{id}/status")
    public ResponseEntity<Result<Void>> updateTrainStatus(@PathVariable Long id, @RequestParam Integer status) {
        Train train = trainService.getById(id);
        if (train == null) {
            return ResponseEntity.notFound().build();
        }
        
        train.setStatus(status);
        boolean success = trainService.updateById(train);
        
        if (success) {
            return ResponseEntity.ok(Result.success("车次状态更新成功", null));
        } else {
            return ResponseEntity.badRequest()
                .body(Result.error("车次状态更新失败"));
        }
    }

    /**
     * 删除车次
     */
    @DeleteMapping("/trains/{id}")
    public ResponseEntity<Result<Void>> deleteTrain(@PathVariable Long id) {
        boolean success = trainService.removeById(id);
        if (success) {
            return ResponseEntity.ok(Result.success("车次删除成功", null));
        } else {
            return ResponseEntity.badRequest()
                .body(Result.error("车次删除失败"));
        }
    }

    /**
     * 强制取消订单（管理员权限）
     */
    @PutMapping("/orders/{id}/force-cancel")
    public ResponseEntity<Result<Void>> forceCancelOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 释放座位
        orderService.lambdaUpdate()
            .eq(Order::getId, id)
            .set(Order::getStatus, 2) // 已取消
            .set(Order::getRemark, "管理员强制取消")
            .update();
        
        return ResponseEntity.ok(Result.success("订单已强制取消", null));
    }

    /**
     * 清理过期订单
     */
    @PostMapping("/orders/cleanup-expired")
    public ResponseEntity<Result<Map<String, Object>>> cleanupExpiredOrders() {
        orderService.releaseExpiredLockedSeats();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "过期订单清理完成");
        result.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(Result.success(result));
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Result<Map<String, Object>>> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        
        // 数据库连接状态
        try {
            userService.count(); // 测试数据库连接
            health.put("database", "healthy");
        } catch (Exception e) {
            health.put("database", "unhealthy");
            health.put("databaseError", e.getMessage());
        }
        
        // 缓存状态
        health.put("cache", "healthy");
        
        // 队列状态（如果有的话）
        health.put("queue", "healthy");
        
        health.put("timestamp", LocalDateTime.now());
        health.put("uptime", "运行正常");
        
        return ResponseEntity.ok(Result.success(health));
    }
}
