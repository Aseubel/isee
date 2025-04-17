package com.aseubel.isee.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.aseubel.isee.dao.ImageMapper;
import com.aseubel.isee.pojo.entity.Image;
import com.aseubel.isee.service.ImageService;
import com.aseubel.isee.util.AliOSSUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static com.aseubel.isee.common.constant.Constant.APP;

/**
 * @author Aseubel
 * @date 2025/4/14 下午11:34
 */
@Service
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    @Autowired
    private AliOSSUtil aliOSSUtil;

    @Autowired
    private ImageMapper imageMapper;

    @Override
    public Image executeDetect(Image originImage) throws ClientException {
        String path = runModel(originImage.getImageUrl());

        Image resultImage = uploadImage(originImage.getImage());
        return resultImage;
    }

    @Override
    public byte[] simpleDownload(String imageUrl) throws ClientException {
        return aliOSSUtil.download(imageUrl.substring(imageUrl.indexOf(APP)));
    }

    public Image uploadImage(MultipartFile image) throws ClientException {
        Image imageEntity = new Image(image);
        String url = aliOSSUtil.upload(image, imageEntity.imageObjectName());
        imageEntity.setImageUrl(url);
        imageMapper.insert(imageEntity);
        return imageEntity;
    }

    private Image uploadImage(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            log.error("Python脚本执行失败，未生成结果文件");
            return null;
        }

        Image imageEntity = new Image();
        String url = null;
        try {
            url = aliOSSUtil.upload(new FileInputStream(file), imageEntity.imageObjectName());
        } catch (ClientException e) {
            log.error("OSS上传失败", e);
            return null;
        } catch (FileNotFoundException e) {
            log.error("Python脚本执行失败，未找到结果文件", e);
            return null;
        }
        imageEntity.setImageUrl(url);
        imageMapper.insert(imageEntity);
        boolean deleted = file.delete();

        if (deleted) {
            log.info("临时文件已删除: {}", imagePath);
        } else {
            log.warn("临时文件删除失败: {}", imagePath);
        }
        return imageEntity;
    }

    private String runModel(String imageUrl) {
//        try {
//            // 启动进程
//            Process process = processBuilder.start();
//
//            // 读取Python脚本的输出
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            StringBuilder output = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//
//            // 等待进程结束，并获取退出值
//            int exitCode = process.waitFor();
//            System.out.println("Python脚本执行完毕，退出码: " + exitCode);
//            System.out.println("Python脚本的输出结果: " + output.toString().trim());
//            return output.toString().trim();
//        } catch (IOException | InterruptedException e) {
//            log.error("Python脚本执行失败", e);
//        }
        return null;
    }
}
