package com.railbooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.railbooking.entity.Order;
import com.railbooking.entity.Seat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Map<String, Object> createOrder(Long userId, Long trainId, String seatType, String passengerName,
                                     String passengerIdCard, String travelDate);

    /**
     * 查询用户订单列表
     */
    List<Order> getUserOrders(Long userId);

    /**
     * 查询用户订单详情
     */
    Order getOrderById(Long orderId, Long userId);

    /**
     * 支付订单
     */
    boolean payOrder(Long orderId, Long userId);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, Long userId);

    /**
     * 退票
     */
    boolean refundOrder(Long orderId, Long userId);

    /**
     * 检查并释放超时的锁定座位
     */
    void releaseExpiredLockedSeats();
}
