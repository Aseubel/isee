package com.aseubel.isee.pojo.dto;

import com.aseubel.isee.common.annotation.FieldDesc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author Aseubel
 * @date 2025/4/22 下午8:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequest implements Serializable {

    @FieldDesc(name = "图片本体")
    private MultipartFile image;

    @FieldDesc(name = "农场")
    private String farm;

    @FieldDesc(name = "区域")
    private String area;
}
