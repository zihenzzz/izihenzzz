package com.railbooking.controller;

import com.railbooking.entity.Station;
import com.railbooking.service.TrainService;
import com.railbooking.util.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车站控制器
 */
@RestController
@RequestMapping("/api/v1/stations")
public class StationController {

    private final TrainService trainService;

    public StationController(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * 获取所有车站列表
     */
    @GetMapping
    public ResponseEntity<Result<List<String>>> getAllStations() {
        // 这里简化处理，实际应该从数据库查询
        List<String> stations = List.of(
                "北京", "上海", "广州", "深圳", "杭州",
                "南京", "武汉", "成都", "重庆", "西安",
                "天津", "苏州", "长沙", "郑州", "青岛"
        );
        return ResponseEntity.ok(Result.success(stations));
    }
}
