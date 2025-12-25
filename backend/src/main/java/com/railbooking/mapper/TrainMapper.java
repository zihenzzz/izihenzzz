package com.railbooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.railbooking.entity.Train;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 车次Mapper接口
 */
@Mapper
public interface TrainMapper extends BaseMapper<Train> {

    /**
     * 根据条件搜索车次
     */
    @Select("SELECT * FROM trains WHERE status = 1 " +
            "AND departure_station_name = #{departureStation} " +
            "AND arrival_station_name = #{arrivalStation} " +
            "AND operate_date_start <= #{travelDate} " +
            "AND operate_date_end >= #{travelDate} " +
            "ORDER BY departure_time ASC")
    List<Train> searchTrains(
            @Param("departureStation") String departureStation,
            @Param("arrivalStation") String arrivalStation,
            @Param("travelDate") LocalDate travelDate
    );
}
