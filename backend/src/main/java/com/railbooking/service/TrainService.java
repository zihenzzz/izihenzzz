package com.railbooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.railbooking.entity.Train;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 车次服务接口
 */
public interface TrainService extends IService<Train> {

    /**
     * 搜索车次
     */
    List<Train> searchTrains(String departureStation, String arrivalStation, LocalDate travelDate);

    /**
     * 根据ID获取车次详情
     */
    Train getTrainById(Long id);

    /**
     * 添加车次
     */
    boolean addTrain(Train train);

    /**
     * 更新车次
     */
    boolean updateTrain(Train train);

    /**
     * 获取车次余票信息
     */
    Map<String, Object> getTrainTicketInfo(Long trainId, LocalDate travelDate);

    /**
     * 初始化车次座位（定时任务使用）
     */
    void initTrainSeats(Long trainId, LocalDate travelDate);
}
