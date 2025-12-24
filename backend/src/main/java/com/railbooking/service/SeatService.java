package com.railbooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.railbooking.entity.Seat;

import java.time.LocalDate;
import java.util.List;

/**
 * 座位服务接口
 */
public interface SeatService extends IService<Seat> {

    /**
     * 获取指定车次和日期的可用座位
     */
    List<Seat> getAvailableSeats(Long trainId, LocalDate travelDate, String seatType);

    /**
     * 获取座位余票数量
     */
    Integer getTicketCount(Long trainId, LocalDate travelDate, String seatType);

    /**
     * 锁定座位
     */
    boolean lockSeat(Long seatId);

    /**
     * 释放座位
     */
    boolean releaseSeat(Long seatId);

    /**
     * 标记座位为已售
     */
    boolean markSeatSold(Long seatId);
}
