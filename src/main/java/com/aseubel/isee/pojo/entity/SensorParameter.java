package com.aseubel.isee.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sensorParameter")
public class SensorParameter {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer sensorTypeId;
    private String name;
    private String displayName;
    private String sign;
    private String registerAddr;
    private String scale;
}