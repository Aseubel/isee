package com.aseubel.isee.dao;

import com.aseubel.isee.pojo.entity.Sensor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SensorMapper extends BaseMapper<Sensor> {
    @Select("SELECT s.* FROM sensors s LEFT JOIN commInterfaces c ON s.comInterfaceId = c.ID " +
            "WHERE (#{areaId} IS NULL OR c.ID = #{areaId}) AND (#{sensorTypeId} IS NULL OR s.sensorTypeId = #{sensorTypeId})")
    List<Sensor> findByAreaAndType(@Param("areaId") Integer areaId, @Param("sensorTypeId") Integer sensorTypeId);
}