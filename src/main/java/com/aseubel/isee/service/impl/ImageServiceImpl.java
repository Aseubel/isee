package com.aseubel.isee.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.aseubel.isee.dao.ImageMapper;
import com.aseubel.isee.pojo.entity.Image;
import com.aseubel.isee.service.ImageService;
import com.aseubel.isee.util.AliOSSUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;

import static com.aseubel.isee.common.constant.Constant.APP;

/**
 * @author Aseubel
 * @date 2025/4/14 下午11:34
 */
@Service
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    @Value("${model.exePath}")
    private String exePath;
    @Value("${model.origin-dist-path}")
    private String originDistPath;
    @Value("${model.result-dist-path}")
    private String resultDistPath;
    @Value("${model.origin-img-path}")
    private String originImgPath;
    @Value("${model.result-img-path}")
    private String resultImgPath;
    @Value("${model.img-name}")
    private String imgName;

    @Autowired
    private AliOSSUtil aliOSSUtil;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ProcessBuilder processBuilder;



    @Override
    public Image executeDetect(Image originImage) throws ClientException, IOException {
        runPython();
        String path = resultImgPath;

        Image resultImage = uploadImage(path);
//        // 删除临时文件
//        boolean deleted = deleteTempFile();
//        if (deleted) {
//            log.info("临时文件已删除！");
//        } else {
//            log.warn("临时文件删除失败！");
//        }
        return resultImage;
    }

    private boolean deleteTempFile() throws IOException {
        // 指定存储目录
        File uploadDir = new File(originDistPath);
        File resultDir = new File(resultDistPath);
        // 确保目录存在
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("无法创建目录: " + uploadDir.getAbsolutePath());
        }
        if (!resultDir.exists() && !resultDir.mkdirs()) {
            throw new IOException("无法创建目录: " + uploadDir.getAbsolutePath());
        }
        // 指定文件名
        String filename = imgName;
        // 构建目标文件路径
        File uploadFile = new File(uploadDir, filename);
        File resultFile = new File(resultDir, filename);
        // 删除文件
        return uploadFile.delete() && resultFile.delete();
    }

    @Override
    public byte[] simpleDownload(String imageUrl) throws ClientException {
        return aliOSSUtil.download(imageUrl.substring(imageUrl.indexOf(APP)));
    }

    private Image uploadImage(MultipartFile image) throws ClientException {
        Image imageEntity = new Image(image);
        String url = aliOSSUtil.upload(image, imageEntity.imageObjectName());
        imageEntity.setImageUrl(url);
        imageMapper.insert(imageEntity);
        return imageEntity;
    }

    @Override
    public Image saveAndUploadImage(MultipartFile image) throws IOException, ClientException {
        // 指定存储目录
        File uploadDir = new File(originDistPath);
        // 确保目录存在
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("无法创建目录: " + uploadDir.getAbsolutePath());
        }
        // 指定文件名
        String filename = imgName;
        // 构建目标文件路径
        File targetFile = new File(uploadDir, filename);
        // 转存文件到目标位置
        image.transferTo(targetFile);

        Image imageEntity = new Image(image);
        String url = aliOSSUtil.upload(image, imageEntity.imageObjectName());
        imageEntity.setImageUrl(url);
        imageMapper.insert(imageEntity);
        return imageEntity;
    }

    private Image uploadImage(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            log.error("模型运行检测失败，未生成结果文件");
            return null;
        }

        Image imageEntity = new Image();
        String url = null;
        try {
            url = aliOSSUtil.upload(new FileInputStream(file), imageEntity.jpgImageObjectName());
        } catch (ClientException e) {
            log.error("OSS上传失败", e);
            return null;
        } catch (FileNotFoundException e) {
            log.error("模型运行检测失败，未找到结果文件");
            return null;
        }
        imageEntity.setImageUrl(url);
        imageMapper.insert(imageEntity);
//        boolean deleted = file.delete();
//
//        if (deleted) {
//            log.info("临时文件已删除: {}", imagePath);
//        } else {
//            log.warn("临时文件删除失败: {}", imagePath);
//        }
        return imageEntity;
    }

    private void runPython() {
        try {
            // 启动进程
            Process process = processBuilder.start();

            // 读取Python脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待进程结束，并获取退出值
            int exitCode = process.waitFor();
            System.out.println("Python脚本执行完毕，退出码: " + exitCode);
            System.out.println("Python脚本的输出结果: " + output.toString().trim());
        } catch (IOException | InterruptedException e) {
            log.error("Python脚本执行失败", e);
        }
    }

    private String runModel() {
        try {
            // 创建ProcessBuilder实例
            ProcessBuilder pb = new ProcessBuilder(exePath);

            // 设置工作目录（可选，默认项目根目录）
//			pb.directory(new File("C:/parent/folder/of/exe"));

            // 启动进程
            Process process = pb.start();

            // 捕获输出流（防止进程阻塞）
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line, path=null;
            while ((line = reader.readLine()) != null) {
                System.out.println("[EXE Output] " + line);
//                if (line.contains("Results saved to ")) {
//
//                }
            }
            // 等待程序执行完成
            int exitCode = process.waitFor();

            path = resultImgPath; // /data/run_headless/runs/train/R-C.jpg
            System.out.println("Exe执行完成，退出码: " + exitCode);
            return path;
        } catch (IOException | InterruptedException e) {
            log.error("Exe执行失败", e);
        }
        return null;
    }
}
