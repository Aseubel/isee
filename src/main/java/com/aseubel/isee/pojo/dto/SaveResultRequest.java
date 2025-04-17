package com.aseubel.isee.pojo.dto;


import com.aseubel.isee.common.annotation.FieldDesc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Aseubel
 * @date 2025/4/16 上午11:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveResultRequest implements Serializable {

    @FieldDesc(name = "原图id")
    private String originImageId;

    @FieldDesc(name = "检测结果图片id")
    private String resultImageId;

    @FieldDesc(name = "结果类型")
    private Integer type;

    @FieldDesc(name = "农场")
    private String farm;

    @FieldDesc(name = "地区")
    private String area;

}
