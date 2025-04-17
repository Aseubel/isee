package com.aseubel.isee.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aseubel
 * @date 2025/4/16 下午12:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResponse {

    private String imageId;

    private String imageUrl;
}
