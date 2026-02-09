package com.cloudsquare.coss.api.lecture.dto;

public record UploadInitResponse(
        String objectKey,
        String presignedPutUrl,
        long expiresInSeconds
) {
}
