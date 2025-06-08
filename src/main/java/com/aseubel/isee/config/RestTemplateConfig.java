package com.aseubel.isee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000); // 设置连接超时时间（毫秒）
        requestFactory.setReadTimeout(30000);    // 设置读取超时时间（毫秒）
        return new RestTemplate(requestFactory);
    }
}
