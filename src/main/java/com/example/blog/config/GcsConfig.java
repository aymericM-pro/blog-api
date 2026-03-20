package com.example.blog.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
@EnableConfigurationProperties(GcsProperties.class)
public class GcsConfig {

    @Bean
    public Storage googleCloudStorage(GcsProperties props) throws IOException {
        if (props.credentialsPath() != null && !props.credentialsPath().isBlank()) {
            log.info("GCS: using explicit service account credentials from {}", props.credentialsPath());
            ServiceAccountCredentials credentials =
                    ServiceAccountCredentials.fromStream(new FileInputStream(props.credentialsPath()));
            return StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        }
        log.info("GCS: using Application Default Credentials");
        return StorageOptions.getDefaultInstance().getService();
    }
}
