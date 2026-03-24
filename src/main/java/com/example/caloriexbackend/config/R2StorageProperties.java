package com.example.caloriexbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage.r2")
public record R2StorageProperties(
        boolean enabled,
        String endpoint,
        String accessKeyId,
        String secretAccessKey,
        String bucket,
        String publicBaseUrl
) {
}
