package com.aseubel.isee.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author Aseubel
 * @date 2025/4/16 下午12:23
 */
@Configuration
@EnableConfigurationProperties(ProcessBuilderConfigProperties.class)
public class ProcessBuilderConfig {

    @Bean(name = "processBuilder")
    public ProcessBuilder processBuilder(ProcessBuilderConfigProperties properties) {
        // 创建ProcessBuilder实例
        ProcessBuilder processBuilder = new ProcessBuilder("python", properties.getName());
        // 设置工作目录为Python脚本所在的目录
        processBuilder.directory(new File(properties.getPath()));
        return processBuilder;
    }
}
