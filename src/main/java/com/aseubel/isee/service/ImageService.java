package com.aseubel.isee.service;

import com.aliyuncs.exceptions.ClientException;
import com.aseubel.isee.pojo.entity.Image;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Aseubel
 * @date 2025/4/14 下午11:35
 */
public interface ImageService extends IService<Image> {

    /**
     * 上传图片
     * @param image 图片文件
     * @return 图片对象
     */
    Image uploadImage(MultipartFile image) throws ClientException;

    /**
     * 执行图片检测
     * @param originImage 原图
     * @return 检测结果图片
     */
    Image executeDetect(Image originImage) throws ClientException;

    /**
     * 下载图片
     * @param imageUrl 图片URL
     * @return 图片字节数组
     */
    byte[] simpleDownload(String imageUrl) throws ClientException;
}
