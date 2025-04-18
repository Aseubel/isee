package com.aseubel.isee.pojo.entity;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.aseubel.isee.common.constant.Constant.APP;


/**
 * @author Aseubel
 * @date 2025/4/14 下午10:58
 */
@Data
@AllArgsConstructor
@Builder
@TableName("image")
public class Image {

    private long id;

    @TableId
    private String imageId;

    private String imageUrl;

    @TableField(exist = false)
    private int type;

    @TableField(exist = false)
    private String farm;

    @TableField(exist = false)
    private String area;

    @TableField(exist = false)
    private String group;

    @TableField(exist = false)
    private MultipartFile image;

    public Image(MultipartFile image) {
        this.image = image;
        this.generateImageId();
    }

    public Image() {
        this.generateImageId();
    }

    public void generateImageId() {
        this.imageId = IdUtil.randomUUID().replace("-", "");
    }

    /**
     * 获取在OSS中的文件名称（在类型文件夹下）
     */
    public String imageObjectName() {
        StringBuilder objectName = new StringBuilder();
        objectName.append(APP).append("/")
                .append(imageId)
                .append(Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf(".")));
        return objectName.toString();
    }

    /**
     * 获取在OSS中的文件名称（在类型文件夹下）
     */
    public String jpgImageObjectName() {
        StringBuilder objectName = new StringBuilder();
        objectName.append(APP).append("/")
                .append(imageId)
                .append(".jpg");
        return objectName.toString();
    }

}
