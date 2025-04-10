package com.aseubel.isee.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;
    private String username;
    private String password;
    private Integer admin;
    private Integer areaId;
    private Boolean isEnabled;

    @TableField(exist = false)
    private String areaName;
}