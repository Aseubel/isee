package com.aseubel.isee.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Aseubel
 * @date 2025/6/7 上午1:44
 */
@Component
@Getter
@ConfigurationProperties(prefix = "detect.server", ignoreInvalidFields = true)
public class DetectServerProperties {
    private String internalIp = "127.0.0.1"; // 内网IP
    private int timeout = 10000; // 超时时间，毫秒
}
