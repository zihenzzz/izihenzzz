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
    @Select("""
        <script>
        SELECT * FROM seats
        WHERE train_id = #{trainId}
        AND travel_date = #{travelDate}
        AND status = 0
        AND seat_type = #{seatType}
        ORDER BY carriage_no ASC, seat_no ASC
        </script>
        """)
    List<Seat> findAvailableSeats(
            @Param("trainId") Long trainId,
            @Param("travelDate") LocalDate travelDate,
            @Param("seatType") String seatType
    );

    /**
     * 查询座位余票数量
     */
    @Select("""
        <script>
        SELECT COUNT(*) FROM seats
        WHERE train_id = #{trainId}
        AND travel_date = #{travelDate}
        AND status = 0
        AND seat_type = #{seatType}
        </script>
        """)
    Integer countAvailableSeats(
            @Param("trainId") Long trainId,
            @Param("travelDate") LocalDate travelDate,
            @Param("seatType") String seatType
    );

    /**
     * 锁定座位（乐观锁方式）
     */
    @Update("""
        UPDATE seats
        SET status = 1, update_time = NOW()
        WHERE id = #{seatId}
        AND status = 0
        """)
    Integer lockSeat(@Param("seatId") Long seatId);

    /**
     * 释放座位（支付超时时使用）
     */
    @Update("""
        UPDATE seats
        SET status = 0, update_time = NOW()
        WHERE id = #{seatId}
        AND status = 1
        """)
    Integer releaseSeat(@Param("seatId") Long seatId);

    /**
     * 标记座位为已售
     */
    @Update("""
        UPDATE seats
        SET status = 2, update_time = NOW()
        WHERE id = #{seatId}
        """)
    Integer markSeatSold(@Param("seatId") Long seatId);

    /**
     * 根据车次ID和日期批量初始化座位
     */
    @Select("""
        <script>
        INSERT INTO seats (train_id, train_code, carriage_no, seat_no, seat_type, status, travel_date, price, create_time, update_time)
        SELECT #{trainId}, #{trainCode}, c.carriage_no, s.seat_no, s.seat_type, 0, #{travelDate}, #{price}, NOW(), NOW()
        FROM carriage_config c
        CROSS JOIN seat_config s
        WHERE c.train_type = #{trainType}
        AND s.seat_type IN ('SECOND_CLASS', 'FIRST_CLASS')
        </script>
        """)
    Integer initSeatsForTrainAndDate(
            @Param("trainId") Long trainId,
            @Param("trainCode") String trainCode,
            @Param("trainType") String trainType,
            @Param("travelDate") LocalDate travelDate,
            @Param("price") BigDecimal price
    );
}
