package com.aseubel.isee.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.aseubel.isee.dao.ImageMapper;
import com.aseubel.isee.pojo.entity.Image;
import com.aseubel.isee.service.ImageService;
import com.aseubel.isee.util.AliOSSUtil;
import com.aseubel.isee.util.CustomMultipartFile;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.aseubel.isee.common.constant.Constant.APP;
import static com.aseubel.isee.common.constant.ImageStatus.*;

/**
 * @author Aseubel
 * @date 2025/4/14 下午11:34
 */
@Service
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    @Value("${model.script.path}")
    private String scriptPath;
    @Value("${model.script.name}")
    private String scriptName;
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

    @Override
    public Image executeDetect(Image originImage) throws ClientException, IOException {
        // 执行检测脚本，生成结果图片
        runPythonByPost();

        // 拿到扩展名
        String oFileName = originImage.getImageUrl();
        String imageType = oFileName.substring(oFileName.lastIndexOf("."));

        // 获取结果图片
        String path = resultDistPath + "/" + imgName + imageType;
        File file = new File(path);
        if (!file.exists()) {
            imageMapper.update(new UpdateWrapper<Image>()
                    .set("status", DETECTED_NO_PEST.value())
                    .eq("image_id", originImage.getImageId())
            );
            return null;
        } else {
            imageMapper.update(new UpdateWrapper<Image>()
                    .set("status", DETECTED_WITH_PEST.value())
                    .eq("image_id", originImage.getImageId())
            );
        }

        // 上传结果图片到OSS
        Image resultImage = new Image(new CustomMultipartFile(FileUtil.readBytes(path), oFileName));
        resultImage.setStatus(RESULT_IMAGE.value());
        resultImage = uploadImage(resultImage);

        // 删除临时图片，因为我们要根据有没有结果图片来判断是否有害虫
        if (deleteTempDist()) {
            log.info("删除临时文件夹成功");
        } else {
            log.error("删除临时文件夹失败");
        }
        return resultImage;
    }

    private boolean deleteTempDist() {
        File originDir = new File(originDistPath);
        File resultDir = new File(resultDistPath);
        // 封装目录删除逻辑
        return deleteDirectoryRecursively(originDir) &&
                deleteDirectoryRecursively(resultDir);
    }

    /**
     * 递归删除目录及子内容
     * @param dir 要删除的目录
     * @return true表示完全删除，false表示删除失败
     */
    private boolean deleteDirectoryRecursively(File dir) {
        if (dir == null || !dir.exists()) {
            return true; // 目录不存在视为删除成功
        }
        // 如果是文件直接删除
        if (dir.isFile()) {
            return dir.delete();
        }
        // 递归删除子目录
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!deleteDirectoryRecursively(file)) {
                    return false; // 子文件/目录删除失败则终止
                }
            }
        }
        // 最后删除空目录
        return dir.delete();
    }

    private boolean deleteTempFile(String imageType) throws IOException {
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
        String filename = imgName + imageType;
        // 构建目标文件路径
        File uploadFile = new File(uploadDir, filename);
        File resultFile = new File(resultDir, filename);
        // 删除文件
        return (!uploadFile.exists() || uploadFile.delete()) && (!resultFile.exists() || resultFile.delete());
    }

    @Override
    public byte[] simpleDownload(String imageUrl) throws ClientException {
        return aliOSSUtil.download(imageUrl.substring(imageUrl.indexOf(APP)));
    }

    @Override
    public Image uploadImage(Image image) throws ClientException {
        Image imageEntity = new Image(image);
        new Thread(() -> {
            try {
                aliOSSUtil.upload(image.getImage(), imageEntity.imageObjectName());
            } catch (ClientException e) {
                throw new RuntimeException(e);
            }
        }).start();
        imageEntity.ossUrl();
        imageMapper.insert(imageEntity);
        return imageEntity;
    }

    @Override
    public void saveImage(Image image) throws IOException, ClientException {
        // 指定存储目录
        File uploadDir = new File(originDistPath);
        // 确保目录存在
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("无法创建目录: " + uploadDir.getAbsolutePath());
        }
        // 指定文件名
        String oFileName = image.getImageUrl();
        String filename = imgName + oFileName.substring(oFileName.lastIndexOf("."));
        // 构建目标文件路径
        File targetFile = new File(uploadDir, filename);
        // 转存文件到目标位置
        image.getImage().transferTo(targetFile);
    }

    private void runPythonByPost() {
        HttpUtil.createPost("http://localhost:8000/predict").timeout(3000).execute();
    }

    private void runPython() {
        try {
            // 创建ProcessBuilder实例
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptName);
            // 设置工作目录为Python脚本所在的目录
            processBuilder.directory(new File(scriptPath));
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
