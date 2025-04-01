package com.aseubel.isee.dao;

import com.aseubel.isee.pojo.entity.AlertNotification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlertNotificationMapper extends BaseMapper<AlertNotification> {

    @Select("SELECT s.DisplayName as sensorName, sp.DisplayName as sensorParamName, a.* " +
            "FROM alert_notifications a " +
            "LEFT JOIN sensors s ON a.sensor_id = s.ID " +
            "LEFT JOIN sensorParameter sp ON a.sensor_param_id = sp.ID " +
            "WHERE a.area_id = #{areaId} AND a.status = 0 " +
            "ORDER BY a.alert_time DESC")
    List<AlertNotification> getAlertsByAreaId(@Param("areaId") Integer areaId);
}