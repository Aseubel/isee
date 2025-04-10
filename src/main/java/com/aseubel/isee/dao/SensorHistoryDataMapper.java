package com.aseubel.isee.dao;

import com.aseubel.isee.pojo.entity.SensorHistoryData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SensorHistoryDataMapper extends BaseMapper<SensorHistoryData> {
    @Select("<script>" +
            "SELECT s.DisplayName as sensorName, p.DisplayName as paramName, p.Sign as paramSign, h.* FROM sensorHistoryData h " +
            "LEFT JOIN sensors s ON h.sensorId = s.ID " +
            "LEFT JOIN commInterfaces c ON s.comInterfaceId = c.ID " +
            "LEFT JOIN sensorParameter p ON h.SensorParamId = p.ID " +
            "WHERE 1=1 " +
            "<if test='startTime!= null'>AND h.dataTime BETWEEN #{startTime} AND #{endTime} </if> " +
            "<if test='areaId!= null'>AND c.ID = #{areaId} </if> " +
            "<if test='sensorTypeId!= null'>AND s.sensorTypeId = #{sensorTypeId} </if> " +
            "ORDER BY h.dataTime " +
            "</script>")
    Page<SensorHistoryData> findHistoryData(
            Page<SensorHistoryData> page,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("areaId") Integer areaId,
            @Param("sensorTypeId") Integer sensorTypeId);

    @Select("<script>" +
            "SELECT s.DisplayName as sensorName, p.DisplayName as paramName, p.Sign as paramSign, h.* FROM sensorHistoryData h " +
            "LEFT JOIN sensors s ON h.sensorId = s.ID " +
            "LEFT JOIN commInterfaces c ON s.comInterfaceId = c.ID " +
            "LEFT JOIN sensorParameter p ON h.SensorParamId = p.ID " +
            "WHERE 1=1 " +
            "<if test='startTime!= null'>AND h.dataTime BETWEEN #{startTime} AND #{endTime} </if> " +
            "<if test='areaId!= null'>AND c.ID = #{areaId} </if> " +
            "ORDER BY h.dataTime " +
            "</script>")
    List<SensorHistoryData> findHistoryData(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("areaId") Integer areaId);
}