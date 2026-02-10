package com.cloudsquare.coss.api.lecture.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
        URI endpoint = validateAndGetEndpoint(properties);

        return S3Client.builder()
                .endpointOverride(endpoint)
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .build();
    }

    @Bean
    S3Presigner s3Presigner(ObjectStorageProperties properties) {
        URI endpoint = validateAndGetEndpoint(properties);

        return S3Presigner.builder()
                .endpointOverride(endpoint)
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .build();
    }

    private URI validateAndGetEndpoint(ObjectStorageProperties properties) {
        List<String> missing = new ArrayList<>();

        requireConfigured(properties.getEndpoint(), "NCP_S3_ENDPOINT (or NCP_ENDPOINT)", missing);
        requireConfigured(properties.getBucket(), "NCP_OBJECT_STORAGE_BUCKET (or NCP_BUCKET)", missing);
        requireConfigured(properties.getRegion(), "NCP_REGION (or NCP_S3_REGION)", missing);
        requireConfigured(properties.getAccessKey(), "NCP_ACCESS_KEY (or NCP_S3_ACCESS_KEY)", missing);
        requireConfigured(properties.getSecretKey(), "NCP_SECRET_KEY (or NCP_S3_SECRET_KEY)", missing);

        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required Object Storage environment variables: " + String.join(", ", missing));
        }

        return URI.create(properties.getEndpoint().trim());
    }

    private void requireConfigured(String value, String envName, List<String> missing) {
        if (value == null || value.isBlank() || value.contains("${")) {
            missing.add(envName);
        }
    }
}
