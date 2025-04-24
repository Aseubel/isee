package com.aseubel.isee.pojo.entity;

import cn.hutool.core.util.IdUtil;
import com.aseubel.isee.common.annotation.FieldDesc;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.aseubel.isee.common.constant.Constant.*;


/**
 * @author Aseubel
 * @date 2025/4/14 下午10:58
 */
@Data
@AllArgsConstructor
@Builder
@TableName("image")
public class Image {

    @FieldDesc(name = "主键id")
    private long id;

    @TableId
    @FieldDesc(name = "图片ID")
    private String imageId;

    @FieldDesc(name = "图片URL")
    private String imageUrl;

    @FieldDesc(name = "状态，-1-结果图;0-原图未检测;1-已检测无害虫;2-已检测有害虫")
    private int status;

    @FieldDesc(name = "农场")
    private String farm;

    @FieldDesc(name = "地区")
    private String area;

    @FieldDesc(name = "创建时间")
    private LocalDateTime createTime;

    @FieldDesc(name = "图片文件")
    @TableField(exist = false)
    private MultipartFile image;

    public Image(MultipartFile image) {
        this.image = image;
        this.generateImageId();
    }

    public Image(Image image) {
        this.id = image.getId();
        this.imageId = image.getImageId();
        this.imageUrl = image.getImageUrl();
        this.status = image.getStatus();
        this.farm = image.getFarm();
        this.area = image.getArea();
        this.image = image.getImage();
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

    /**
     * 获取oss的url
     */
    public String ossUrl() {
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(BUCKET_NAME)
                .append(".")
                .append(ENDPOINT)
                .append("/")
                .append(imageObjectName());
        this.imageUrl = stringBuilder.toString();
        return this.imageUrl;
    }

}
