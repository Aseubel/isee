package com.aseubel.isee.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sensors")
public class Sensor {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String displayName;
    private Integer enable;
    private Integer slaveId;
    private Integer sensorTypeId;
    private Integer comInterfaceId;
}