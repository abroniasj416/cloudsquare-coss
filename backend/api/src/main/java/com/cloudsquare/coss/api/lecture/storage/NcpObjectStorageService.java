package com.cloudsquare.coss.api.lecture.storage;

import java.nio.file.Path;
import java.time.Duration;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class NcpObjectStorageService implements ObjectStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ObjectStorageProperties properties;

    public NcpObjectStorageService(
            S3Client s3Client,
            S3Presigner s3Presigner,
            ObjectStorageProperties properties
    ) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.properties = properties;
    }

    @Override
    public String generatePresignedPutUrl(String objectKey, Duration expires) {
        String normalizedObjectKey = normalizeObjectKey(objectKey);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(normalizedObjectKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expires)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return presigned.url().toString();
    }

    @Override
    public String generatePresignedGetUrl(String objectKey, Duration expires) {
        String normalizedObjectKey = normalizeObjectKey(objectKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(normalizedObjectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expires)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
        return presigned.url().toString();
    }

    @Override
    public boolean objectExists(String objectKey) {
        String normalizedObjectKey = normalizeObjectKey(objectKey);

        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(normalizedObjectKey)
                    .build());
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public void downloadToFile(String objectKey, Path targetFile) {
        String normalizedObjectKey = normalizeObjectKey(objectKey);

        s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(normalizedObjectKey)
                        .build(),
                ResponseTransformer.toFile(targetFile));
    }

    @Override
    public void uploadFile(String objectKey, Path sourceFile, String contentType) {
        String normalizedObjectKey = normalizeObjectKey(objectKey);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(normalizedObjectKey)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromFile(sourceFile));
    }

    private String normalizeObjectKey(String objectKey) {
        String key = stripSlashes(objectKey);
        if (key.isBlank()) {
            throw new IllegalArgumentException("objectKey must not be blank");
        }

        String prefix = stripSlashes(properties.getVideoPrefix());
        if (prefix.isBlank()) {
            return key;
        }
        if (key.startsWith(prefix + "/")) {
            return key;
        }
        return prefix + "/" + key;
    }

    private String stripSlashes(String value) {
        if (value == null) {
            return "";
        }

        String stripped = value.trim();
        while (stripped.startsWith("/")) {
            stripped = stripped.substring(1);
        }
        while (stripped.endsWith("/")) {
            stripped = stripped.substring(0, stripped.length() - 1);
        }
        return stripped;
    }
}
