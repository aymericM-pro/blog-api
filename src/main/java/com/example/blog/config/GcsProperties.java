package com.example.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.gcs")
public record GcsProperties(
        String bucketName,
        String credentialsPath,
        String publicUrlPrefix
) {}
