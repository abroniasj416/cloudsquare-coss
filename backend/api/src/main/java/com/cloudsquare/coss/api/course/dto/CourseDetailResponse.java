package com.cloudsquare.coss.api.course.dto;

public record CourseDetailResponse(
        Long courseId,
        String courseCode,
        String title,
        String instructorName,
        Integer capacity,
        Integer enrolledCount
) {
}
