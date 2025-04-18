package com.aseubel.isee.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aseubel
 * @date 2025/4/18 上午12:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("commInterfaces")
public class CommInterface {

    @TableId
    private Integer id;

    private String name;
}
