package com.aseubel.isee.pojo.dto;


import com.aseubel.isee.common.annotation.FieldDesc;
import com.aseubel.isee.pojo.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

    public ImageResponse(Image image) {
        this.imageId = image.getImageId();
        this.imageUrl = image.getImageUrl();
    }

}
