package com.cloudsquare.coss.api.course.dto;

public record CourseSummaryResponse(
        Long courseId,
        String courseCode,
        String title,
        String instructorName,
        Integer capacity,
        Integer enrolledCount
) {
}
