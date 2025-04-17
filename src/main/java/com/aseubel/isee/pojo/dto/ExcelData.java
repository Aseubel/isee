package com.aseubel.isee.pojo.dto;

import com.aseubel.isee.pojo.entity.SensorHistoryData;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Aseubel
 * @date 2025/4/10 下午8:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelData {
    private String sensorName;
    private String paramName;
    private Double value;
    private String paramSign;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataTime;

    public ExcelData(SensorHistoryData data) {
        this.sensorName = data.getSensorName();
        this.paramName = data.getParamName();
        this.value = data.getValue();
        this.paramSign = data.getParamSign();
        this.dataTime = data.getDataTime();
    }

}
