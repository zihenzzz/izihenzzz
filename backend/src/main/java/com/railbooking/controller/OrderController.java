package com.railbooking.controller;

import com.railbooking.entity.Order;
import com.railbooking.entity.Seat;
import com.railbooking.entity.User;
import com.railbooking.service.OrderService;
import com.railbooking.service.SeatService;
import com.railbooking.util.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final SeatService seatService;

    public OrderController(OrderService orderService, SeatService seatService) {
        this.orderService = orderService;
        this.seatService = seatService;
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<Result<Map<String, Object>>> createOrder(@RequestBody CreateOrderRequest request) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = (User) authentication.getPrincipal();

        Map<String, Object> result = orderService.createOrder(
                user.getId(),
                request.getTrainId(),
                request.getSeatType(),
                request.getPassengerName(),
                request.getPassengerIdCard(),
                request.getTravelDate()
        );

        if ((boolean) result.get("success")) {
            return ResponseEntity.ok(Result.success("订单创建成功", result));
        } else {
            return ResponseEntity.badRequest()
                    .body(Result.error((String) result.get("message")));
        }
    }

    /**
     * 获取我的订单列表
     */
    @GetMapping
    public ResponseEntity<Result<List<Order>>> getMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Order> orders = orderService.getUserOrders(user.getId());
        return ResponseEntity.ok(Result.success(orders));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<Order>> getOrderDetail(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Order order = orderService.getOrderById(id, user.getId());
        if (order != null) {
            return ResponseEntity.ok(Result.success(order));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<Result<Void>> payOrder(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        boolean success = orderService.payOrder(id, user.getId());
        if (success) {
            return ResponseEntity.ok(Result.success("支付成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(Result.error("支付失败，订单可能已超时或状态不正确"));
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Result<Void>> cancelOrder(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        boolean success = orderService.cancelOrder(id, user.getId());
        if (success) {
            return ResponseEntity.ok(Result.success("订单取消成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(Result.error("取消失败"));
        }
    }

    /**
     * 退票
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<Result<Void>> refundOrder(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        boolean success = orderService.refundOrder(id, user.getId());
        if (success) {
            return ResponseEntity.ok(Result.success("退票成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(Result.error("退票失败"));
        }
    }

    /**
     * 获取可用座位列表
     */
    @GetMapping("/seats")
    public ResponseEntity<Result<List<Seat>>> getAvailableSeats(
            @RequestParam Long trainId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate travelDate,
            @RequestParam String seatType) {

        List<Seat> seats = seatService.getAvailableSeats(trainId, travelDate, seatType);
        return ResponseEntity.ok(Result.success(seats));
    }

    /**
     * 创建订单请求对象
     */
    @lombok.Data
    public static class CreateOrderRequest {
        private Long trainId;
        private String seatType;
        private String passengerName;
        private String passengerIdCard;
        private String travelDate;
    }
}
