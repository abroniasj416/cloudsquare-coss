package com.cloudsquare.coss.api.lecture.dto;

import java.time.Instant;

public record LectureDetailResponse(
        Long id,
        String title,
        String description,
        String thumbnailUrl,
        String videoStatus,
        Instant createdAt
) {
}
