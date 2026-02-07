package com.cloudsquare.coss.api.course.dto;

public record EnrollmentSummaryResponse(
        Long enrollmentId,
        Long courseId,
        String courseCode,
        String courseTitle,
        String status,
        String enrolledAt
) {
}
