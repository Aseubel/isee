package com.aseubel.isee.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.aseubel.isee.pojo.dto.ExcelData;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

import static com.aseubel.isee.common.constant.Constant.*;

@Service
@RequiredArgsConstructor
public class SensorDataServiceImpl extends ServiceImpl<SensorHistoryDataMapper, SensorHistoryData>
        implements ISensorDataService {

    private final SensorHistoryDataMapper sensorHistoryDataMapper;
    private final SensorDataMapper sensorDataMapper;
    private final IRedisService redissonService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;

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
        return sensorHistoryDataMapper.pageFindHistoryData(page, startTime, endTime, areaId, sensorTypeId);
    }

    @Override
    public void getHistoryData(LocalDateTime queryTime, Integer areaId, HttpServletResponse response) {
        // 记录一下开始时间，方便等下遍历
        LocalDateTime startTime = queryTime.minus(FILE_HISTORY_TIME, FILE_HISTORY_TIME_UNIT);

        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), ExcelData.class).build()) {
            // 这里注意 如果同一个sheet只要创建一次
            WriteSheet writeSheet = EasyExcel.writerSheet("sheet1").build();
//            int i=1;
//            for (LocalDateTime t = queryTime; t.isAfter(startTime); i++,t = t.minus(THREAD_POOL_TIME_SPAN, THREAD_POOL_TIME_UNIT)) {
//                long t1 = System.currentTimeMillis();
//                List<ExcelData> data = convert2ExcelData(
//                        sensorHistoryDataMapper.findHistoryData(t.minus(THREAD_POOL_TIME_SPAN, THREAD_POOL_TIME_UNIT), t, areaId)
//                );
//                long duration = System.currentTimeMillis() - t1;
//                System.out.println(i + "查询耗时：" + duration + "ms");
//                long t2 = System.currentTimeMillis();
//                excelWriter.write(data, writeSheet);
//                long duration2 = System.currentTimeMillis() - t2;
//                System.out.println(i + "写入耗时：" + duration2 + "ms");
//            }

            // 生成时间序列任务
            List<LocalDateTime> timePoints = new ArrayList<>();
            for (LocalDateTime t = queryTime; t.isAfter(startTime); t = t.minus(THREAD_POOL_TIME_SPAN, THREAD_POOL_TIME_UNIT)) {
                timePoints.add(t);
            }
            // 并行获取数据（保持时间顺序）
            List<Future<Pair<Integer, List<ExcelData>>>> futures = new ArrayList<>();
            for (int i = 0; i < timePoints.size(); i++) {
                final int index = i;
                LocalDateTime t = timePoints.get(i);
                futures.add(threadPoolExecutor.submit(() -> {
                    System.out.println(index + ": 开始查询" + t.minus(THREAD_POOL_TIME_SPAN, THREAD_POOL_TIME_UNIT) + "到" + t);
                    long t1 = System.currentTimeMillis();
                    List<ExcelData> data = convert2ExcelData(
                            sensorHistoryDataMapper.findHistoryData(t.minus(THREAD_POOL_TIME_SPAN, THREAD_POOL_TIME_UNIT), t, areaId)
                    );
                    long duration = System.currentTimeMillis() - t1;
                    System.out.println(index + ": 查询耗时" + duration + "ms");
                    return Pair.of(index, data);  // 返回带顺序标记的数据
                }));
            }

            // 按原始顺序写入（阻塞等待）
            futures.stream()
                    .sorted(Comparator.comparingInt(f -> {
                        try {
                            return f.get().getKey();
                        } catch (Exception e) {
                            throw new RuntimeException("Parallel processing failed", e);
                        }
                    }))
                    .forEachOrdered(f -> {
                        try {
                            excelWriter.write(f.get().getValue(), writeSheet);
                        } catch (InterruptedException | ExecutionException e) {
                            log.error("数据写入异常:", e);
                            Thread.currentThread().interrupt();
                        }
                    });

        } catch (Exception e) {
            log.error("下载历史数据并导出失败：", e);
        }
    }

    @Override
    public void test(LocalDateTime queryTime, Integer areaId) {
        long startTime = System.currentTimeMillis();
        sensorHistoryDataMapper.findHistoryData(queryTime.minus(THREAD_POOL_TIME_SPAN, THREAD_POOL_TIME_UNIT), queryTime, areaId);
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("耗时：" + duration + "ms");
    }

    // 转换为ExcelData，大概在10w左右甚至更高，如果CPU多核可以考虑使用stream并行流
    private List<ExcelData> convert2ExcelData(List<SensorHistoryData> data) {
//        long startTime = System.currentTimeMillis();
        List<ExcelData> excelData = new ArrayList<>(data.size());
        for (SensorHistoryData datum : data) {
            excelData.add(new ExcelData(datum));
        }
//        List<ExcelData> excelData = data.parallelStream()
//                .map(datum -> {
//                    ExcelData ed = new ExcelData();
//                    ed.setSensorName(datum.getSensorName());
//                    ed.setParamName(datum.getParamName());
//                    ed.setValue(datum.getValue());
//                    ed.setParamSign(datum.getParamSign());
//                    ed.setDataTime(datum.getDataTime());
//                    return ed;
//                })
//                .collect(Collectors.toCollection(() -> new ArrayList<>(data.size())));
//        long duration = System.currentTimeMillis() - startTime;
//        System.out.println(data.size() + " 条历史数据转换为ExcelData耗时：" + duration + "ms");
        return excelData;
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