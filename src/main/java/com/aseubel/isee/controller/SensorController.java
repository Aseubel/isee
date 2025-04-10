package com.aseubel.isee.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.aseubel.isee.common.Response;
import com.aseubel.isee.common.annotation.RequirePermission;
import com.aseubel.isee.pojo.entity.SensorData;
import com.aseubel.isee.pojo.entity.SensorHistoryData;
import com.aseubel.isee.service.ISensorDataService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sensor")
@RequiredArgsConstructor
public class SensorController {

    private final ISensorDataService sensorDataService;

    /**
     * 获取实时数据
     */
    @GetMapping("/realtime")
    @RequirePermission(checkArea = true)
    public Response<Map<String, List<SensorData>>> getRealtimeData(
            @RequestParam Integer areaId) {
        if (areaId == null) {
            return Response.fail("Area id is required.");
        }
        return Response.success(sensorDataService.getRealtimeData(areaId));
    }

    /**
     * 获取历史数据
     */
    @GetMapping("/history")
    @RequirePermission(checkArea = true)
    public Response<Page<SensorHistoryData>> getHistoryData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer areaId,
            @RequestParam(required = false) Integer sensorTypeId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<SensorHistoryData> page = new Page<>(current, size);
        return Response.success(sensorDataService.getHistoryData(startTime, endTime, areaId, sensorTypeId, page));
    }

    /**
     * 获取近一个月的历史数据
     */
    @GetMapping("/history/lastmonth")
    @RequirePermission(checkArea = true)
    public Response<Map<String, List<SensorData>>> getLastMonthHistoryData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer areaId) {
        return Response.success(sensorDataService.getLastMonthHistoryData(startTime, endTime, areaId));
    }
}