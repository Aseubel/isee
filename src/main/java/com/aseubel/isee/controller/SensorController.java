package com.aseubel.isee.controller;

import com.aseubel.isee.common.Response;
import com.aseubel.isee.common.annotation.RequirePermission;
import com.aseubel.isee.pojo.entity.SensorData;
import com.aseubel.isee.pojo.entity.SensorHistoryData;
import com.aseubel.isee.service.ISensorDataService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.aseubel.isee.common.constant.Constant.FILE_HISTORY_TIME;
import static com.aseubel.isee.common.constant.Constant.FILE_HISTORY_TIME_UNIT;

@Slf4j
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
     * 导出历史数据
     */
    @GetMapping("/history/download")
//    @RequirePermission(checkArea = true)
    public void downloadHistoryData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime queryTime,
            @RequestParam(required = false) Integer areaId,
            HttpServletResponse response) throws UnsupportedEncodingException {
//        sensorDataService.test(queryTime, areaId);
        // 默认查询当前时间
        if (queryTime == null) {
            queryTime = LocalDateTime.now();
        }
        // 记录一下开始结束时间
        LocalDateTime startTime = queryTime.minus(FILE_HISTORY_TIME, FILE_HISTORY_TIME_UNIT);
        LocalDateTime endTime = queryTime;

        String name = "history_" + startTime.toString().replace(" ", "_") + "_" + endTime.toString().replace(" ", "_");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8).replaceAll("\\+", "%20").replaceAll(":", "");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        sensorDataService.getHistoryData(queryTime, areaId, response);
    }
}