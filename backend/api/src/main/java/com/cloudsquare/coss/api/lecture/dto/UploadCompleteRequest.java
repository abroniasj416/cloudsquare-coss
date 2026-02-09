package com.cloudsquare.coss.api.lecture.dto;

public record UploadCompleteRequest(
        String objectKey,
        long sizeBytes,
        String contentType
) {
}
