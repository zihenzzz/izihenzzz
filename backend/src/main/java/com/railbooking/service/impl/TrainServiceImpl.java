package com.railbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.railbooking.entity.Seat;
import com.railbooking.entity.Train;
import com.railbooking.mapper.SeatMapper;
import com.railbooking.mapper.TrainMapper;
import com.railbooking.service.TrainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 车次服务实现类
 */
@Slf4j
@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService {

    private final TrainMapper trainMapper;
    private final SeatMapper seatMapper;

    public TrainServiceImpl(TrainMapper trainMapper, SeatMapper seatMapper) {
        this.trainMapper = trainMapper;
        this.seatMapper = seatMapper;
    }

    @Override
    public List<Train> searchTrains(String departureStation, String arrivalStation, LocalDate travelDate) {
        List<Train> trains = trainMapper.searchTrains(departureStation, arrivalStation, travelDate);

        // 补充余票信息
        trains.forEach(train -> {
            Map<String, Object> ticketInfo = getTrainTicketInfo(train.getId(), travelDate);
            train.setExtra(ticketInfo);
        });

        return trains;
    }

    @Override
    public Train getTrainById(Long id) {
        return trainMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean addTrain(Train train) {
        // 生成车次编号
        String trainCode = generateTrainCode(train.getTrainType());
        train.setTrainCode(trainCode);

        // 计算运行时长
        if (train.getDuration() == null) {
            train.setDuration(calculateDuration(train.getDepartureTime(), train.getArrivalTime(), train.getRunDays()));
        }

        // 设置默认状态
        if (train.getStatus() == null) {
            train.setStatus(1);
        }

        int result = trainMapper.insert(train);
        if (result > 0) {
            log.info("车次添加成功: {}", trainCode);
            // 初始化未来30天的座位
            initSeatsForTrain(train);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateTrain(Train train) {
        int result = trainMapper.updateById(train);
        if (result > 0) {
            log.info("车次更新成功: {}", train.getTrainCode());
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getTrainTicketInfo(Long trainId, LocalDate travelDate) {
        Map<String, Object> ticketInfo = new HashMap<>();

        // 查询各类座位余票数量
        Integer secondClassCount = seatMapper.countAvailableSeats(trainId, travelDate, "SECOND_CLASS");
        Integer firstClassCount = seatMapper.countAvailableSeats(trainId, travelDate, "FIRST_CLASS");
        Integer hardSleeperCount = seatMapper.countAvailableSeats(trainId, travelDate, "HARD_SLEEPER");
        Integer softSleeperCount = seatMapper.countAvailableSeats(trainId, travelDate, "SOFT_SLEEPER");

        ticketInfo.put("secondClass", secondClassCount != null ? secondClassCount : 0);
        ticketInfo.put("firstClass", firstClassCount != null ? firstClassCount : 0);
        ticketInfo.put("hardSleeper", hardSleeperCount != null ? hardSleeperCount : 0);
        ticketInfo.put("softSleeper", softSleeperCount != null ? softSleeperCount : 0);

        return ticketInfo;
    }

    @Override
    @Transactional
    public void initTrainSeats(Long trainId, LocalDate travelDate) {
        Train train = trainMapper.selectById(trainId);
        if (train == null) {
            log.warn("车次不存在: {}", trainId);
            return;
        }

        // 初始化各类座位
        initSeatType(trainId, train.getTrainCode(), travelDate, train.getTrainType(),
                "SECOND_CLASS", train.getSecondClassPrice(), 10);
        initSeatType(trainId, train.getTrainCode(), travelDate, train.getTrainType(),
                "FIRST_CLASS", train.getFirstClassPrice(), 5);
        initSeatType(trainId, train.getTrainCode(), travelDate, train.getTrainType(),
                "HARD_SLEEPER", train.getHardSleeperPrice(), 5);
        initSeatType(trainId, train.getTrainCode(), travelDate, train.getTrainType(),
                "SOFT_SLEEPER", train.getSoftSleeperPrice(), 3);

        log.info("车次 {} 的 {} 座位初始化完成", train.getTrainCode(), travelDate);
    }

    private void initSeatType(Long trainId, String trainCode, LocalDate travelDate,
                              String trainType, String seatType, BigDecimal price, int carriageCount) {
        for (int carriage = 1; carriage <= carriageCount; carriage++) {
            int seatCount = getSeatCountByType(seatType);
            for (int i = 1; i <= seatCount; i++) {
                String seatNo = formatSeatNo(i);
                Seat seat = new Seat();
                seat.setTrainId(trainId);
                seat.setTrainCode(trainCode);
                seat.setCarriageNo(carriage);
                seat.setSeatNo(seatNo);
                seat.setSeatType(seatType);
                seat.setStatus(0);
                seat.setTravelDate(travelDate);
                seat.setPrice(price);
                seatMapper.insert(seat);
            }
        }
    }

    private int getSeatCountByType(String seatType) {
        return switch (seatType) {
            case "SECOND_CLASS" -> 80; // 4排×20列
            case "FIRST_CLASS" -> 40;  // 4排×10列
            case "HARD_SLEEPER" -> 60; // 6排×10列
            case "SOFT_SLEEPER" -> 36; // 4排×9列
            default -> 40;
        };
    }

    private String formatSeatNo(int index) {
        int row = (index - 1) / 4 + 1;
        char seat = (char) ('A' + (index - 1) % 4);
        return row + String.valueOf(seat);
    }

    private String generateTrainCode(String trainType) {
        String prefix = switch (trainType) {
            case "G" -> "G";
            case "D" -> "D";
            case "K" -> "K";
            case "T" -> "T";
            default -> "G";
        };
        return prefix + String.format("%03d", new Random().nextInt(900) + 100);
    }

    private int calculateDuration(LocalTime departureTime, LocalTime arrivalTime, Integer runDays) {
        int departureMinutes = departureTime.getHour() * 60 + departureTime.getMinute();
        int arrivalMinutes = arrivalTime.getHour() * 60 + arrivalTime.getMinute() + runDays * 24 * 60;
        return arrivalMinutes - departureMinutes;
    }

    private void initSeatsForTrain(Train train) {
        LocalDate startDate = train.getOperateDateStart();
        LocalDate endDate = train.getOperateDateEnd();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            initTrainSeats(train.getId(), date);
        }
    }
}
