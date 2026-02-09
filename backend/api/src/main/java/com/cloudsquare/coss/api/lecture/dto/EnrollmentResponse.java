package com.cloudsquare.coss.api.lecture.dto;

import java.time.Instant;

public record EnrollmentResponse(
        Long enrollmentId,
        Long lectureId,
        String userId,
        Instant createdAt
) {
}
