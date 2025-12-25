package com.railbooking.controller;

import com.railbooking.entity.Station;
import com.railbooking.mapper.StationMapper;
import com.railbooking.util.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车站控制器
 */
@RestController
@RequestMapping("/api/v1/stations")
public class StationController {

    private final StationMapper stationMapper;

    public StationController(StationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    /**
     * 获取所有车站列表
     */
    @GetMapping
    public ResponseEntity<Result<List<Station>>> getAllStations() {
        List<Station> stations = stationMapper.selectList(null);
        return ResponseEntity.ok(Result.success(stations));
    }
}
