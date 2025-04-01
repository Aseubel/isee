package com.aseubel.isee.dao;

import com.aseubel.isee.pojo.entity.SensorHistoryData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SensorHistoryDataMapper extends BaseMapper<SensorHistoryData> {
    @Select("SELECT s.DisplayName as sensorName, p.DisplayName as paramName, p.Sign as paramSign, h.* FROM sensorHistoryData h " +
            "LEFT JOIN sensors s ON h.sensorId = s.ID " +
            "LEFT JOIN commInterfaces c ON s.comInterfaceId = c.ID " +
            "LEFT JOIN sensorParameter p ON h.SensorParamId = p.ID " +
            "WHERE h.dataTime BETWEEN #{startTime} AND #{endTime} " +
            "AND (#{areaId} IS NULL OR c.ID = #{areaId}) " +
            "AND (#{sensorTypeId} IS NULL OR s.sensorTypeId = #{sensorTypeId}) " +
            "ORDER BY h.dataTime DESC")
    List<SensorHistoryData> findHistoryData(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("areaId") Integer areaId,
            @Param("sensorTypeId") Integer sensorTypeId);
}