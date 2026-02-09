package com.cloudsquare.coss.api.lecture.dto;

public record UploadCompleteResponse(
        Long lectureVideoId,
        String status
) {
}
