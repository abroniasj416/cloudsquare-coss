package com.cloudsquare.coss.api.lecture.storage;

import java.nio.file.Path;
import java.time.Duration;

public interface ObjectStorageService {

    String generatePresignedPutUrl(String objectKey, Duration expires);

    String generatePresignedGetUrl(String objectKey, Duration expires);

    boolean objectExists(String objectKey);

    void downloadToFile(String objectKey, Path targetFile);

    void uploadFile(String objectKey, Path sourceFile, String contentType);
}
