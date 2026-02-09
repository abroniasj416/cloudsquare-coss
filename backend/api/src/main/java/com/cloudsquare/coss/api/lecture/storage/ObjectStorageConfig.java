package com.cloudsquare.coss.api.lecture.storage;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class ObjectStorageConfig {

    @Bean
    S3Client s3Client(ObjectStorageProperties properties) {
        validateRequiredStorageConfig(properties);

        return S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .build();
    }

    @Bean
    S3Presigner s3Presigner(ObjectStorageProperties properties) {
        validateRequiredStorageConfig(properties);

        return S3Presigner.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .build();
    }

    private void validateRequiredStorageConfig(ObjectStorageProperties properties) {
        requireConfigured(properties.getEndpoint(), "NCP_S3_ENDPOINT");
        requireConfigured(properties.getBucket(), "NCP_OBJECT_STORAGE_BUCKET");
        requireConfigured(properties.getAccessKey(), "NCP_ACCESS_KEY");
        requireConfigured(properties.getSecretKey(), "NCP_SECRET_KEY");
    }

    private void requireConfigured(String value, String envName) {
        if (value == null || value.isBlank() || value.contains("${")) {
            throw new IllegalStateException(envName + " is required. Set it in environment or backend/api/.env");
        }
    }
}