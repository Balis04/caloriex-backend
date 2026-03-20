package com.example.caloryxbackend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(R2StorageProperties.class)
public class StorageConfig {

    @Bean
    public S3Client s3Client(R2StorageProperties properties) {
        String accessKeyId = hasText(properties.accessKeyId()) ? properties.accessKeyId() : "dummy-access-key";
        String secretAccessKey = hasText(properties.secretAccessKey()) ? properties.secretAccessKey() : "dummy-secret-key";

        return S3Client.builder()
                .applyMutation(builder -> {
                    if (hasText(properties.endpoint())) {
                        builder.endpointOverride(URI.create(properties.endpoint()));
                    }
                })
                .region(Region.of("auto"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        accessKeyId,
                                        secretAccessKey
                                )
                        )
                )
                .forcePathStyle(true)
                .build();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
