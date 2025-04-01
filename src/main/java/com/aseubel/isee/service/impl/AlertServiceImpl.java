package com.aseubel.isee.service.impl;

import com.aseubel.isee.dao.AlertNotificationMapper;
import com.aseubel.isee.dao.SensorDataMapper;
import com.aseubel.isee.pojo.entity.AlertNotification;
import com.aseubel.isee.pojo.entity.SensorData;
import com.aseubel.isee.redis.IRedisService;
import com.aseubel.isee.service.IAlertService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertServiceImpl extends ServiceImpl<AlertNotificationMapper, AlertNotification> implements IAlertService {

    private final SensorDataMapper sensorDataMapper;
    private final AlertNotificationMapper alertMapper;
    private final IRedisService redissonService;
    private final Map<String, SseEmitter> sseEmitters;

    @Scheduled(fixedRate = 180000) // 每 3 分钟检查一次
    @Override
    public void checkAndSendAlerts() {
        System.out.println("checkAndSendAlerts");
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        // 查询最近30分钟内的数据
        List<SensorData> recentData = sensorDataMapper.getRealData();

        // 查询未完成的预警通知
        List<AlertNotification> alerts = getAlertsUnCompleted();

        // 按照id过滤掉已经有预警通知的数据
        recentData.removeIf(data -> alerts.stream()
               .anyMatch(alert -> alert.getSensorId().equals(data.getSensorId()) && alert.getSensorParamId().equals(data.getSensorParamId())));

        // 检查每个传感器的最新数据时间
        recentData.forEach(data -> {
            if (data.getUpdatedAt().isBefore(thirtyMinutesAgo)) {
                // 创建预警通知
                AlertNotification alert = new AlertNotification();
                alert.setSensorId(data.getSensorId());
                alert.setSensorParamId(data.getSensorParamId());
                alert.setAreaId(data.getAreaId());
                alert.setAlertTime(data.getUpdatedAt());
                alert.setStatus(0);
                save(alert);

                // 将预警信息存入Redis，用于前端SSE获取
                String alertKey = "alert:" + data.getSensorId() + ":" + data.getSensorParamId();
                redissonService.setValue(alertKey, alert, 3600 * 1000); // 1小时过期

                // 向所有订阅的管理员推送预警通知
                sseEmitters.forEach((userId, emitter) -> {
                        safeSend(emitter, SseEmitter.event()
                                .name("alert")
                                .data(data.getSensorName() + " 的 " + data.getParamName() + " 三十分钟未更新数据！"));
                });

                // TODO: 集成短信通知服务
                // sendSmsNotification(alert);
            }
        });
    }

    @Override
    public List<AlertNotification> getAlertsByArea(Integer areaId) {
        // 根据地区ID查询预警通知
        return alertMapper.getAlertsByAreaId(areaId);
    }

    @Override
    public void updateAlertStatus(Integer alertId, Integer status) {
        AlertNotification alert = getById(alertId);
        if (alert != null) {
            alert.setStatus(status);
            updateById(alert);

            // 更新Redis中的预警状态
            String alertKey = "alert:" + alert.getSensorId() + ":" + alert.getSensorParamId();
            redissonService.setValue(alertKey, alert, 3600 * 1000);
        }
    }

    private List<AlertNotification> getAlertsUnCompleted() {
        return list(new LambdaQueryWrapper<AlertNotification>()
                .eq(AlertNotification::getStatus, 0));
    }

    @Async("threadPoolExecutor")
    public void safeSend(SseEmitter emitter, Object data) {
        try {
            emitter.send(data);
        } catch (IOException e) {
            log.warn("Failed to send data to emitter", e);
            emitter.complete();
        }
    }
}