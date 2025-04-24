package com.aseubel.isee.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Aseubel
 * @description 图片状态枚举类
 * @date 2025/4/22 下午7:40
 */
@Getter
@AllArgsConstructor
public enum ImageStatus {

    RESULT_IMAGE(-1, "结果图"),
    ORIGINAL_UNDETECTED(0, "原图未检测"),
    DETECTED_NO_PEST(1, "已检测无害虫"),
    DETECTED_WITH_PEST(2, "已检测有害虫");

    private final int statusCode;
    private final String description;

    public int value() {
        return this.statusCode;
    }

}
