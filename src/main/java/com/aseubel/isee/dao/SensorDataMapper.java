package com.aseubel.isee.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.aseubel.isee.pojo.entity.SensorData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface SensorDataMapper extends BaseMapper<SensorData> {

    @Select("SELECT c.ID as areaId, s.DisplayName as sensorName, p.DisplayName as paramName, p.Sign as paramSign, d.* " +
            "FROM sensorData d " +
            "LEFT JOIN sensors s ON d.sensorId = s.ID " +
            "LEFT JOIN commInterfaces c ON s.comInterfaceId = c.ID " +
            "LEFT JOIN sensorParameter p ON d.SensorParamId = p.ID " +
            "WHERE (#{areaId} IS NULL OR c.ID = #{areaId}) " +
            "AND d.UpdatedAt IS NOT NULL ")
    List<SensorData> getRealDataByAreaId(@Param("areaId") Integer areaId);

    @Select("SELECT c.ID as areaId, s.DisplayName as sensorName, p.DisplayName as paramName, p.Sign as paramSign, d.* " +
            "FROM sensorData d " +
            "LEFT JOIN sensors s ON d.sensorId = s.ID " +
            "LEFT JOIN commInterfaces c ON s.comInterfaceId = c.ID " +
            "LEFT JOIN sensorParameter p ON d.SensorParamId = p.ID " +
            "WHERE d.UpdatedAt IS NOT NULL ")
    List<SensorData> getRealData();
}