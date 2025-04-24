package com.aseubel.isee.pojo.dto;


import com.aseubel.isee.common.annotation.FieldDesc;
import com.aseubel.isee.pojo.entity.Image;
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
 * @date 2025/4/16 下午5:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponse implements Serializable {

    @FieldDesc(name = "原图id")
    private String imageId;

    @FieldDesc(name = "原图url")
    private String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @FieldDesc(name = "检测时间")
    private LocalDateTime createTime;

    public ImageResponse(Image image) {
        this.imageId = image.getImageId();
        this.imageUrl = image.getImageUrl();
        this.createTime = image.getCreateTime();
    }

}
