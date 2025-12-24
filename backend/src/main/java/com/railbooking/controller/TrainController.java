package com.railbooking.controller;

import com.railbooking.entity.Train;
import com.railbooking.service.TrainService;
import com.railbooking.util.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 车次控制器
 */
@RestController
@RequestMapping("/api/v1/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * 搜索车次
     */
    @GetMapping("/search")
    public ResponseEntity<Result<List<Train>>> searchTrains(
            @RequestParam String departureStation,
            @RequestParam String arrivalStation,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate travelDate) {

        List<Train> trains = trainService.searchTrains(departureStation, arrivalStation, travelDate);
        return ResponseEntity.ok(Result.success(trains));
    }

    /**
     * 获取车次详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<Train>> getTrainById(@PathVariable Long id) {
        Train train = trainService.getTrainById(id);
        if (train != null) {
            return ResponseEntity.ok(Result.success(train));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取车次余票信息
     */
    @GetMapping("/{id}/info")
    public ResponseEntity<Result<Map<String, Object>>> getTrainTicketInfo(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate travelDate) {

        Map<String, Object> ticketInfo = trainService.getTrainTicketInfo(id, travelDate);
        return ResponseEntity.ok(Result.success(ticketInfo));
    }

    /**
     * 添加车次（管理员）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Void>> addTrain(@RequestBody Train train) {
        boolean success = trainService.addTrain(train);
        if (success) {
            return ResponseEntity.ok(Result.success("车次添加成功", null));
        }
        return ResponseEntity.badRequest()
                .body(Result.error("车次添加失败"));
    }

    /**
     * 更新车次（管理员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Void>> updateTrain(@PathVariable Long id, @RequestBody Train train) {
        train.setId(id);
        boolean success = trainService.updateTrain(train);
        if (success) {
            return ResponseEntity.ok(Result.success("车次更新成功", null));
        }
        return ResponseEntity.badRequest()
                .body(Result.error("车次更新失败"));
    }
}
