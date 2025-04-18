package com.aseubel.isee.service;

import com.aliyuncs.exceptions.ClientException;
import com.aseubel.isee.pojo.entity.Image;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Aseubel
 * @date 2025/4/14 下午11:35
 */
public interface ImageService extends IService<Image> {

    /**
     * 上传并保存图片
     * @param image 图片文件
     * @return 图片对象
     */
    Image saveAndUploadImage(MultipartFile image) throws IOException, ClientException;

    /**
     * 执行图片检测
     * @param originImage 原图
     * @return 检测结果图片
     */
    Image executeDetect(Image originImage) throws ClientException, IOException;

    /**
     * 下载图片
     * @param imageUrl 图片URL
     * @return 图片字节数组
     */
    byte[] simpleDownload(String imageUrl) throws ClientException;
}
