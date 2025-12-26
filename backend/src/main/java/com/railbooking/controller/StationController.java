package com.railbooking.controller;

import com.railbooking.entity.Station;
import com.railbooking.mapper.StationMapper;
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

    /**
     * 添加车站（管理员）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Void>> addStation(@RequestBody Station station) {
        int rows = stationMapper.insert(station);
        if (rows > 0) {
            return ResponseEntity.ok(Result.success("车站添加成功", null));
        }
        return ResponseEntity.badRequest().body(Result.error("车站添加失败"));
    }

    /**
     * 更新车站（管理员）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Void>> updateStation(@PathVariable Long id, @RequestBody Station station) {
        station.setId(id);
        int rows = stationMapper.updateById(station);
        if (rows > 0) {
            return ResponseEntity.ok(Result.success("车站更新成功", null));
        }
        return ResponseEntity.badRequest().body(Result.error("车站更新失败"));
    }

    /**
     * 删除车站（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result<Void>> deleteStation(@PathVariable Long id) {
        int rows = stationMapper.deleteById(id);
        if (rows > 0) {
            return ResponseEntity.ok(Result.success("车站删除成功", null));
        }
        return ResponseEntity.badRequest().body(Result.error("车站删除失败"));
    }
}
