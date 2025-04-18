package com.aseubel.isee.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Aseubel
 * @date 2025/4/16 下午12:29
 */
@Data
@ConfigurationProperties(prefix = "model.script", ignoreInvalidFields = true)
public class ProcessBuilderConfigProperties {

    /** 脚本路径文件夹 */
    private String path;

    /** 脚本名称 */
    private String name;
}
