package com.aseubel.isee.service;

import com.aseubel.isee.pojo.entity.AlertNotification;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IAlertService extends IService<AlertNotification> {
    /**
     * 检查传感器数据更新状态并发送预警
     */
    void checkAndSendAlerts();

    /**
     * 获取指定地区的预警通知
     * 
     * @param areaId 地区ID
     * @return 预警通知列表
     */
    List<AlertNotification> getAlertsByArea(Integer areaId);

    /**
     * 更新预警状态
     * 
     * @param alertId 预警ID
     * @param status  新状态
     */
    void updateAlertStatus(Integer alertId, Integer status);
}