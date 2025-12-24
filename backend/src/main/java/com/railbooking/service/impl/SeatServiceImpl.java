package com.railbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.railbooking.entity.Seat;
import com.railbooking.mapper.SeatMapper;
import com.railbooking.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 座位服务实现类
 */
@Slf4j
@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    private final SeatMapper seatMapper;

    public SeatServiceImpl(SeatMapper seatMapper) {
        this.seatMapper = seatMapper;
    }

    @Override
    public List<Seat> getAvailableSeats(Long trainId, LocalDate travelDate, String seatType) {
        return seatMapper.findAvailableSeats(trainId, travelDate, seatType);
    }

    @Override
    public Integer getTicketCount(Long trainId, LocalDate travelDate, String seatType) {
        return seatMapper.countAvailableSeats(trainId, travelDate, seatType);
    }

    @Override
    @Transactional
    public boolean lockSeat(Long seatId) {
        Integer result = seatMapper.lockSeat(seatId);
        if (result > 0) {
            log.info("座位锁定成功: seatId={}", seatId);
            return true;
        }
        log.warn("座位锁定失败，可能已被预订: seatId={}", seatId);
        return false;
    }

    @Override
    @Transactional
    public boolean releaseSeat(Long seatId) {
        Integer result = seatMapper.releaseSeat(seatId);
        if (result > 0) {
            log.info("座位释放成功: seatId={}", seatId);
            return true;
        }
        log.warn("座位释放失败: seatId={}", seatId);
        return false;
    }

    @Override
    @Transactional
    public boolean markSeatSold(Long seatId) {
        Integer result = seatMapper.markSeatSold(seatId);
        if (result > 0) {
            log.info("座位已售出标记成功: seatId={}", seatId);
            return true;
        }
        log.warn("座位已售出标记失败: seatId={}", seatId);
        return false;
    }
}
