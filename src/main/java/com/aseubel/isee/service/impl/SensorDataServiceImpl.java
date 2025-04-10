package com.aseubel.isee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.aseubel.isee.dao.SensorDataMapper;
import com.aseubel.isee.dao.SensorHistoryDataMapper;
import com.aseubel.isee.pojo.entity.SensorData;
import com.aseubel.isee.pojo.entity.SensorHistoryData;
import com.aseubel.isee.redis.IRedisService;
import com.aseubel.isee.service.ISensorDataService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorDataServiceImpl extends ServiceImpl<SensorHistoryDataMapper, SensorHistoryData>
                implements ISensorDataService {

        private final SensorHistoryDataMapper sensorHistoryDataMapper;
        private final SensorDataMapper sensorDataMapper;
        private final IRedisService redissonService;

        @Override
        public Map<String, List<SensorData>> getRealtimeData(Integer areaId) {
                String cacheKey = String.format("realtime:data:%d", areaId);

                // 尝试从缓存获取数据
                Map<String, List<SensorData>> cachedData = (Map<String, List<SensorData>>) redissonService
                                .getValue(cacheKey);
                if (CollectionUtil.isNotEmpty(cachedData)) {
                        return cachedData;
                }

                // 查询最新数据
                List<SensorData> realtimeData = sensorDataMapper.getRealDataByAreaId(areaId);

                // 按传感器ID分组
                Map<String, List<SensorData>> result = realtimeData.stream()
                                .collect(Collectors.groupingBy(SensorData::getSensorName));

                // 缓存数据，设置1分钟过期
                redissonService.setValue(cacheKey, result, 60 * 1000);

                return result;
        }

        @Override
        public Page<SensorHistoryData> getHistoryData(
                        LocalDateTime startTime,
                        LocalDateTime endTime,
                        Integer areaId,
                        Integer sensorTypeId,
                        Page<SensorHistoryData> page) {
                return sensorHistoryDataMapper.findHistoryData(page, startTime, endTime, areaId, sensorTypeId);
        }

        @Override
        public Map<String, List<SensorData>> getLastMonthHistoryData(LocalDateTime startTime, LocalDateTime endTime, Integer areaId) {
                return Map.of();
        }

        @Scheduled(fixedRate = 60000) // 每分钟更新一次缓存
        public void updateRealtimeDataCache() {
                // 清除所有实时数据缓存
                redissonService.remove("realtime:data:*");
        }
}