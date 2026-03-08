package com.knowledge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

/**
 * 文件上传配置类
 * 配置文件上传大小限制和 CORS 跨域设置
 */
@Slf4j
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    /**
     * 配置文件上传大小限制
     * 最大 5MB
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 单个文件最大大小
        factory.setMaxFileSize(DataSize.ofMegabytes(5));
        // 总上传数据最大大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(50));
        log.info("文件上传配置已加载：单文件最大 5MB，总大小最大 50MB");
        return factory.createMultipartConfig();
    }

    /**
     * 配置 CORS 跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
