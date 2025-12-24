package com.railbooking.controller;

import com.railbooking.entity.Station;
import com.railbooking.entity.Train;
import com.railbooking.service.TrainService;
import com.railbooking.util.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 测试控制器 - 用于开发和测试
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final TrainService trainService;

    public TestController(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * 初始化测试数据
     */
    @PostMapping("/init-data")
    public ResponseEntity<Result<String>> initTestData() {
        try {
            // 创建测试车次
            createTestTrains();
            
            return ResponseEntity.ok(Result.success("测试数据初始化成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Result.error("测试数据初始化失败: " + e.getMessage()));
        }
    }

    private void createTestTrains() {
        // G101 北京南 -> 上海虹桥
        Train train1 = new Train();
        train1.setTrainCode("G101");
        train1.setTrainType("G");
        train1.setDepartureStationName("北京南");
        train1.setArrivalStationName("上海虹桥");
        train1.setDepartureTime(LocalTime.of(8, 0));
        train1.setArrivalTime(LocalTime.of(12, 36));
        train1.setRunDays(0);
        train1.setDuration(276);
        train1.setFirstClassPrice(java.math.BigDecimal.valueOf(599));
        train1.setSecondClassPrice(java.math.BigDecimal.valueOf(553));
        train1.setHardSleeperPrice(java.math.BigDecimal.ZERO);
        train1.setSoftSleeperPrice(java.math.BigDecimal.ZERO);
        train1.setTotalSeats(100);
        train1.setOperateDateStart(LocalDate.now());
        train1.setOperateDateEnd(LocalDate.now().plusMonths(3));
        train1.setStatus(1);
        trainService.addTrain(train1);

        // G103 北京南 -> 上海虹桥
        Train train2 = new Train();
        train2.setTrainCode("G103");
        train2.setTrainType("G");
        train2.setDepartureStationName("北京南");
        train2.setArrivalStationName("上海虹桥");
        train2.setDepartureTime(LocalTime.of(9, 0));
        train2.setArrivalTime(LocalTime.of(13, 30));
        train2.setRunDays(0);
        train2.setDuration(270);
        train2.setFirstClassPrice(java.math.BigDecimal.valueOf(599));
        train2.setSecondClassPrice(java.math.BigDecimal.valueOf(553));
        train2.setHardSleeperPrice(java.math.BigDecimal.ZERO);
        train2.setSoftSleeperPrice(java.math.BigDecimal.ZERO);
        train2.setTotalSeats(100);
        train2.setOperateDateStart(LocalDate.now());
        train2.setOperateDateEnd(LocalDate.now().plusMonths(3));
        train2.setStatus(1);
        trainService.addTrain(train2);

        // G105 北京南 -> 上海虹桥
        Train train3 = new Train();
        train3.setTrainCode("G105");
        train3.setTrainType("G");
        train3.setDepartureStationName("北京南");
        train3.setArrivalStationName("上海虹桥");
        train3.setDepartureTime(LocalTime.of(10, 0));
        train3.setArrivalTime(LocalTime.of(14, 20));
        train3.setRunDays(0);
        train3.setDuration(260);
        train3.setFirstClassPrice(java.math.BigDecimal.valueOf(579));
        train3.setSecondClassPrice(java.math.BigDecimal.valueOf(533));
        train3.setHardSleeperPrice(java.math.BigDecimal.ZERO);
        train3.setSoftSleeperPrice(java.math.BigDecimal.ZERO);
        train3.setTotalSeats(100);
        train3.setOperateDateStart(LocalDate.now());
        train3.setOperateDateEnd(LocalDate.now().plusMonths(3));
        train3.setStatus(1);
        trainService.addTrain(train3);

        // D201 武汉 -> 成都东
        Train train4 = new Train();
        train4.setTrainCode("D201");
        train4.setTrainType("D");
        train4.setDepartureStationName("武汉站");
        train4.setArrivalStationName("成都东");
        train4.setDepartureTime(LocalTime.of(7, 30));
        train4.setArrivalTime(LocalTime.of(14, 0));
        train4.setRunDays(0);
        train4.setDuration(390);
        train4.setFirstClassPrice(java.math.BigDecimal.ZERO);
        train4.setSecondClassPrice(java.math.BigDecimal.ZERO);
        train4.setHardSleeperPrice(java.math.BigDecimal.valueOf(180));
        train4.setSoftSleeperPrice(java.math.BigDecimal.valueOf(200));
        train4.setTotalSeats(80);
        train4.setOperateDateStart(LocalDate.now());
        train4.setOperateDateEnd(LocalDate.now().plusMonths(3));
        train4.setStatus(1);
        trainService.addTrain(train4);

        // G301 广州南 -> 深圳北
        Train train5 = new Train();
        train5.setTrainCode("G301");
        train5.setTrainType("G");
        train5.setDepartureStationName("广州南");
        train5.setArrivalStationName("深圳北");
        train5.setDepartureTime(LocalTime.of(8, 30));
        train5.setArrivalTime(LocalTime.of(9, 20));
        train5.setRunDays(0);
        train5.setDuration(50);
        train5.setFirstClassPrice(java.math.BigDecimal.valueOf(99.50));
        train5.setSecondClassPrice(java.math.BigDecimal.valueOf(82));
        train5.setHardSleeperPrice(java.math.BigDecimal.ZERO);
        train5.setSoftSleeperPrice(java.math.BigDecimal.ZERO);
        train5.setTotalSeats(100);
        train5.setOperateDateStart(LocalDate.now());
        train5.setOperateDateEnd(LocalDate.now().plusMonths(3));
        train5.setStatus(1);
        trainService.addTrain(train5);

        // K501 西安北 -> 北京西
        Train train6 = new Train();
        train6.setTrainCode("K501");
        train6.setTrainType("K");
        train6.setDepartureStationName("西安北");
        train6.setArrivalStationName("北京西");
        train6.setDepartureTime(LocalTime.of(18, 0));
        train6.setArrivalTime(LocalTime.of(8, 30));
        train6.setRunDays(1);
        train6.setDuration(870);
        train6.setFirstClassPrice(java.math.BigDecimal.ZERO);
        train6.setSecondClassPrice(java.math.BigDecimal.ZERO);
        train6.setHardSleeperPrice(java.math.BigDecimal.valueOf(200));
        train6.setSoftSleeperPrice(java.math.BigDecimal.valueOf(300));
        train6.setTotalSeats(60);
        train6.setOperateDateStart(LocalDate.now());
        train6.setOperateDateEnd(LocalDate.now().plusMonths(3));
        train6.setStatus(1);
        trainService.addTrain(train6);
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public ResponseEntity<Result<Map<String, Object>>> getSystemInfo() {
        Map<String, Object> info = new java.util.HashMap<>();
        info.put("system", "火车订票系统");
        info.put("version", "1.0.0");
        info.put("author", "MiniMax Agent");
        info.put("timestamp", java.time.LocalDateTime.now());
        info.put("status", "运行正常");
        
        return ResponseEntity.ok(Result.success(info));
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Result<Map<String, String>>> healthCheck() {
        Map<String, String> health = new java.util.HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(Result.success(health));
    }
}
