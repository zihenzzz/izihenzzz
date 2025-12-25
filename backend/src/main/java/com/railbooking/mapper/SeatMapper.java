package com.railbooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.railbooking.entity.Seat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 座位Mapper接口
 */
@Mapper
public interface SeatMapper extends BaseMapper<Seat> {

    /**
     * 查询指定车次和日期的可用座位
     */
    @Select("SELECT * FROM seats WHERE train_id = #{trainId} " +
            "AND travel_date = #{travelDate} AND status = 0 AND seat_type = #{seatType} " +
            "ORDER BY carriage_no ASC, seat_no ASC")
    List<Seat> findAvailableSeats(
            @Param("trainId") Long trainId,
            @Param("travelDate") LocalDate travelDate,
            @Param("seatType") String seatType
    );

    /**
     * 查询座位余票数量
     */
    @Select("SELECT COUNT(*) FROM seats WHERE train_id = #{trainId} " +
            "AND travel_date = #{travelDate} AND status = 0 AND seat_type = #{seatType}")
    Integer countAvailableSeats(
            @Param("trainId") Long trainId,
            @Param("travelDate") LocalDate travelDate,
            @Param("seatType") String seatType
    );

    /**
     * 锁定座位（乐观锁方式）
     */
    @Update("UPDATE seats SET status = 1, update_time = NOW() WHERE id = #{seatId} AND status = 0")
    Integer lockSeat(@Param("seatId") Long seatId);

    /**
     * 释放座位（支付超时时使用）
     */
    @Update("UPDATE seats SET status = 0, update_time = NOW() WHERE id = #{seatId} AND status = 1")
    Integer releaseSeat(@Param("seatId") Long seatId);

    /**
     * 标记座位为已售
     */
    @Update("UPDATE seats SET status = 2, update_time = NOW() WHERE id = #{seatId}")
    Integer markSeatSold(@Param("seatId") Long seatId);
}
