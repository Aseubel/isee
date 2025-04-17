package com.aseubel.isee.pojo.entity;


import com.aseubel.isee.common.annotation.FieldDesc;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Aseubel
 * @date 2025/4/16 上午11:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("result")
public class Result {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String resultId;

    @FieldDesc(name = "原图id")
    private String originImageId;

    @FieldDesc(name = "检测结果图片id")
    private String resultImageId;

    @FieldDesc(name = "检测结果，0-无法判断;1-有害虫;2-无害虫")
    private int type;

    @FieldDesc(name = "农场")
    private String farm;

    @FieldDesc(name = "地区")
    private String area;

    @FieldDesc(name = "创建时间")
    private LocalDateTime detectTime;

}
