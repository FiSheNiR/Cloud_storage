package org.example.cloud_storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(String url, String accessKey, String secretKey) {
}
