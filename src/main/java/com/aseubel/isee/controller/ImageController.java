package com.aseubel.isee.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyuncs.exceptions.ClientException;
import com.aseubel.isee.common.Response;
import com.aseubel.isee.pojo.dto.DetectResponse;
import com.aseubel.isee.pojo.dto.ImageResponse;
import com.aseubel.isee.pojo.dto.QueryImageRequest;
import com.aseubel.isee.pojo.dto.SaveResultRequest;
import com.aseubel.isee.pojo.entity.Image;
import com.aseubel.isee.pojo.entity.Result;
import com.aseubel.isee.service.ImageService;
import com.aseubel.isee.service.ResultService;
import com.aseubel.isee.util.CustomMultipartFile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 图片接口
 *
 * @author Aseubel
 * @date 2025/4/14 下午10:48
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ResultService resultService;

    /**
     * 上传图片执行检测
     */
    @PostMapping("/execute")
    public Response<DetectResponse> execute(MultipartFile image, HttpServletResponse response) throws ClientException, IOException {
        if (StrUtil.isEmpty(image.getOriginalFilename())) {
            response.setStatus(400);
            return null;
        }

        // 将图片进行处理，如转成jpg格式，压缩大小等
        try (InputStream inputStream = image.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(inputStream)
                    .useExifOrientation(true) // 避免额外旋转计算
//                    .size(1024, 1024) // 设置压缩尺寸
//                    .keepAspectRatio(true) // 保持纵横比
                    .scale(1.0) // 等比例缩放
                    .outputQuality(1.0) // 设置输出质量（0.0到1.0之间）
                    .outputFormat("JPEG") // 设置输出格式
                    .toOutputStream(outputStream);
            // 将压缩后的图片转换为Base64字符串
            byte[] compressedBytes = outputStream.toByteArray();
            MultipartFile compressedImage = new CustomMultipartFile(compressedBytes, jpgFileName(image));

            Image originImage = imageService.saveAndUploadImage(compressedImage);
            Image resultImage = imageService.executeDetect(originImage);
            return Response.success(new DetectResponse(originImage, resultImage));
        }
    }

    private String jpgFileName(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        return originalFilename.substring(0, originalFilename.lastIndexOf('.')) + ".jpg";
    }

    /**
     * 保存检测结果
     */
    @PostMapping("/result")
    public Response<?> saveResult(@RequestBody SaveResultRequest request) {
        Result result = Result.builder()
                .originImageId(request.getOriginImageId())
                .resultImageId(request.getResultImageId())
                .type(request.getType())
                .farm(request.getFarm())
                .area(request.getArea())
                .build();
        AtomicBoolean ifExists = new AtomicBoolean(false);
        resultService.query()
                .eq("origin_image_id", request.getOriginImageId())
                .eq("result_image_id", request.getResultImageId())
                .eq("type", request.getType())
                .eq("farm", request.getFarm())
                .eq("area", request.getArea())
                .last("LIMIT 1").oneOpt().ifPresent(
                        r -> ifExists.set(true)
                );
        if (ifExists.get()) {
            return Response.paramFail("请勿重复保存相同的结果");
        }
        resultService.save(result);
        return Response.success();
    }

    /**
     * 查询图片
     */
    @GetMapping("")
    public Response<List<List<ImageResponse>>> queryImages(QueryImageRequest request, HttpServletResponse response) {
        // 1. 查询结果列表
        List<Result> results = resultService.list(
                new QueryWrapper<Result>()
                        .eq("farm", request.getFarm())
                        .eq("area", request.getArea())
        );
        if (CollectionUtil.isEmpty(results)) {
            return Response.success(Collections.emptyList());
        }

        // 提取所有关联的图片ID（合并 origin 和 result）
        List<String> allImageIds = new ArrayList<>();
        results.forEach(result -> {
            allImageIds.add(result.getOriginImageId());
            allImageIds.add(result.getResultImageId());
        });

        // 2. 查询所有关联的图片，并构建 ID → Image 的映射
        Map<String, Image> imageMap = imageService.list(
                new QueryWrapper<Image>().in("image_id", allImageIds)
        ).stream().collect(Collectors.toMap(Image::getImageId, image -> image));

        // 3. 按 Result 分组构建二维数组
        List<List<ImageResponse>> groupedImages = new ArrayList<>();
        for (Result result : results) {
            Image originImg = imageMap.get(result.getOriginImageId());
            Image resultImg = imageMap.get(result.getResultImageId());
            groupedImages.add(imageToResponse(Arrays.asList(originImg, resultImg)));
        }

        return Response.success(groupedImages);
    }

    /**
     * 下载图片
     */
    @GetMapping("/picture")
    public void picture(String filePath, HttpServletResponse response) throws ClientException {
        if (StrUtil.isEmpty(filePath)) {
            response.setStatus(400);
            return;
        }
        byte[] fileBytes = imageService.simpleDownload(filePath);
        // 设置响应头
        response.setContentType("image/jpeg");
        response.setContentLength(fileBytes.length);
        // 写入图片数据
        try (OutputStream os = response.getOutputStream()) {
            os.write(fileBytes);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String executeModel() {
        return "execute";
    }

    private List<ImageResponse> imageToResponse(List<Image> images) {
        return images.stream().map(ImageResponse::new).toList();
    }

}
