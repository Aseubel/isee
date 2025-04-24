package com.aseubel.isee.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Aseubel
 * @date 2025/4/16 上午12:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryImageRequest implements Serializable {

    private String farm;

    private String area;

    private Integer type;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
