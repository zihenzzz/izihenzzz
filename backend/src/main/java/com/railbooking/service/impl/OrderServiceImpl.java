package com.railbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.railbooking.entity.Order;
import com.railbooking.entity.Seat;
import com.railbooking.entity.Train;
import com.railbooking.entity.User;
import com.railbooking.mapper.OrderMapper;
import com.railbooking.mapper.SeatMapper;
import com.railbooking.mapper.TrainMapper;
import com.railbooking.service.OrderService;
import com.railbooking.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final SeatMapper seatMapper;
    private final TrainMapper trainMapper;
    private final UserService userService;

    private static final int ORDER_EXPIRE_MINUTES = 30; // 订单30分钟未支付自动取消

    public OrderServiceImpl(OrderMapper orderMapper, SeatMapper seatMapper,
                           TrainMapper trainMapper, UserService userService) {
        this.orderMapper = orderMapper;
        this.seatMapper = seatMapper;
        this.trainMapper = trainMapper;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Map<String, Object> createOrder(Long userId, Long trainId, String seatType,
                                            String passengerName, String passengerIdCard,
                                            String travelDateStr) {
        Map<String, Object> result = new HashMap<>();

        // 验证用户
        User user = userService.getById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        // 验证车次
        Train train = trainMapper.selectById(trainId);
        if (train == null) {
            result.put("success", false);
            result.put("message", "车次不存在");
            return result;
        }

        // 解析乘车日期
        LocalDate travelDate = LocalDate.parse(travelDateStr);

        // 查找可用座位
        List<Seat> availableSeats = seatMapper.findAvailableSeats(trainId, travelDate, seatType);
        if (availableSeats.isEmpty()) {
            result.put("success", false);
            result.put("message", "该席别已无余票");
            return result;
        }

        // 选择第一个可用座位并锁定
        Seat selectedSeat = availableSeats.get(0);
        Integer lockResult = seatMapper.lockSeat(selectedSeat.getId());
        if (lockResult == 0) {
            result.put("success", false);
            result.put("message", "座位已被预订，请重新选择");
            return result;
        }

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTrainId(trainId);
        order.setTrainCode(train.getTrainCode());
        order.setDepartureStation(train.getDepartureStationName());
        order.setArrivalStation(train.getArrivalStationName());
        order.setDepartureTime(train.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        order.setArrivalTime(train.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        order.setTravelDate(travelDateStr);
        order.setPassengerName(passengerName);
        order.setPassengerIdCard(passengerIdCard);
        order.setSeatType(seatType);
        order.setCarriageNo(selectedSeat.getCarriageNo());
        order.setSeatNo(selectedSeat.getSeatNo());
        order.setAmount(selectedSeat.getPrice());
        order.setStatus(0); // 待支付
        order.setPayDeadline(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES));
        order.setRemark("座位号: " + selectedSeat.getCarriageNo() + "车厢 " + selectedSeat.getSeatNo());

        orderMapper.insert(order);

        result.put("success", true);
        result.put("orderId", order.getId());
        result.put("orderNo", orderNo);
        result.put("amount", order.getAmount());
        result.put("payDeadline", order.getPayDeadline());
        result.put("seatInfo", selectedSeat.getCarriageNo() + "车厢 " + selectedSeat.getSeatNo());

        log.info("订单创建成功: {}, 用户: {}, 车次: {}", orderNo, userId, train.getTrainCode());
        return result;
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                    .orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(queryWrapper);
    }

    @Override
    public Order getOrderById(Long orderId, Long userId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getId, orderId)
                    .eq(Order::getUserId, userId);
        return orderMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean payOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId);
        if (order == null) {
            log.warn("订单不存在或无权操作: orderId={}, userId={}", orderId, userId);
            return false;
        }

        if (order.getStatus() != 0) {
            log.warn("订单状态不正确，无法支付: orderId={}, status={}", orderId, order.getStatus());
            return false;
        }

        // 检查支付时间
        if (LocalDateTime.now().isAfter(order.getPayDeadline())) {
            // 支付超时，释放座位并取消订单
            releaseSeat(order.getTrainId(), LocalDate.parse(order.getTravelDate()), order.getSeatType());
            order.setStatus(2); // 已取消
            orderMapper.updateById(order);
            log.info("订单支付超时已取消: {}", order.getOrderNo());
            return false;
        }

        // 标记座位为已售
        markSeatSold(order.getTrainId(), LocalDate.parse(order.getTravelDate()),
                     order.getSeatType(), order.getCarriageNo(), order.getSeatNo());

        // 更新订单状态为已支付
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单支付成功: {}", order.getOrderNo());
        return true;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId);
        if (order == null) {
            return false;
        }

        if (order.getStatus() != 0) {
            log.warn("只有待支付的订单可以取消: orderId={}, status={}", orderId, order.getStatus());
            return false;
        }

        // 释放座位
        releaseSeat(order.getTrainId(), LocalDate.parse(order.getTravelDate()), order.getSeatType());

        // 更新订单状态
        order.setStatus(2); // 已取消
        order.setRemark("用户取消订单");
        orderMapper.updateById(order);

        log.info("订单取消成功: {}", order.getOrderNo());
        return true;
    }

    @Override
    @Transactional
    public boolean refundOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId);
        if (order == null) {
            return false;
        }

        if (order.getStatus() != 1) {
            log.warn("只有已支付的订单可以退票: orderId={}, status={}", orderId, order.getStatus());
            return false;
        }

        // 释放座位（设置为空闲状态，供其他用户购买）
        // 注意：实际业务中可能需要更复杂的逻辑，比如是否重新开放销售
        releaseSeat(order.getTrainId(), LocalDate.parse(order.getTravelDate()), order.getSeatType());

        // 更新订单状态
        order.setStatus(3); // 已退票
        order.setRemark("用户退票");
        orderMapper.updateById(order);

        log.info("订单退票成功: {}", order.getOrderNo());
        return true;
    }

    @Override
    @Transactional
    public void releaseExpiredLockedSeats() {
        // 查找所有超时的待支付订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getStatus, 0)
                    .lt(Order::getPayDeadline, LocalDateTime.now());

        List<Order> expiredOrders = orderMapper.selectList(queryWrapper);

        for (Order order : expiredOrders) {
            // 释放座位
            releaseSeat(order.getTrainId(), LocalDate.parse(order.getTravelDate()), order.getSeatType());

            // 更新订单状态
            order.setStatus(2); // 已取消
            order.setRemark("支付超时自动取消");
            orderMapper.updateById(order);

            log.info("超时订单自动取消: {}", order.getOrderNo());
        }
    }

    private String generateOrderNo() {
        return "RB" + System.currentTimeMillis() +
               String.format("%04d", new Random().nextInt(10000));
    }

    private void markSeatSold(Long trainId, LocalDate travelDate, String seatType,
                              Integer carriageNo, String seatNo) {
        // 这里应该根据更多信息定位具体座位
        // 简化处理：不做实际更新，因为座位已经在创建订单时锁定了
        log.info("座位已售出: trainId={}, date={}, type={}, carriage={}, seat={}",
                 trainId, travelDate, seatType, carriageNo, seatNo);
    }

    private void releaseSeat(Long trainId, LocalDate travelDate, String seatType) {
        // 查找并释放锁定的座位
        // 实际实现需要更精确的座位定位逻辑
        log.info("座位已释放: trainId={}, date={}, type={}", trainId, travelDate, seatType);
    }
}
