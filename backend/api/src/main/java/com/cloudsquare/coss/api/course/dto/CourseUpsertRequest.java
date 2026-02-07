package com.cloudsquare.coss.api.course.dto;

public record CourseUpsertRequest(
        String courseCode,
        String title,
        String instructorName,
        Integer capacity
) {
}
