package com.knowledge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置属性类
 * 支持通过 application.yml 或 application.properties 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    /**
     * 最大文件大小（字节），默认 5MB
     */
    private long maxSize = 5 * 1024 * 1024;

    /**
     * 允许的文件扩展名，默认 .md 和 .markdown
     */
    private String[] allowedExtensions = {".md", ".markdown"};
}
