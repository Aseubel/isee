package com.aseubel.isee.pojo.dto;


import com.aseubel.isee.common.annotation.FieldDesc;
import com.aseubel.isee.pojo.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Aseubel
 * @date 2025/4/16 上午9:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectResponse implements Serializable {

    @FieldDesc(name = "原图id")
    private String originImageId;

    @FieldDesc(name = "结果图id")
    private String resultImageId;

    @FieldDesc(name = "原图url")
    private String originUrl;

    @FieldDesc(name = "结果图url")
    private String resultUrl;

    public DetectResponse(Image image) {
        this.originImageId = image.getImageId();
        this.originUrl = image.getImageUrl();
    }

    public DetectResponse(Image image1, Image image2) {
        this.originImageId = image1.getImageId();
        this.originUrl = image1.getImageUrl();
        this.resultImageId = image2.getImageId();
        this.resultUrl = image2.getImageUrl();
    }
}
