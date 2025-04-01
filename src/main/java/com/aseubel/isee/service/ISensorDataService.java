package com.aseubel.isee.service;

import com.aseubel.isee.pojo.entity.SensorData;
import com.aseubel.isee.pojo.entity.SensorHistoryData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ISensorDataService extends IService<SensorHistoryData> {
    /**
     * 获取实时数据
     *
     * @param areaId       地区ID
     * @return 传感器最新数据
     */
    Map<String, List<SensorData>> getRealtimeData(Integer areaId);

    /**
     * 分页查询历史数据
     * 
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param areaId       地区ID
     * @param sensorTypeId 传感器类型ID
     * @param page         分页参数
     * @return 分页数据
     */
    Page<SensorHistoryData> getHistoryData(
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer areaId,
            Integer sensorTypeId,
            Page<SensorHistoryData> page);
}