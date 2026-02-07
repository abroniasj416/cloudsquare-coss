package com.cloudsquare.coss.api.course.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudsquare.coss.api.course.dto.CourseSummaryResponse;
import com.cloudsquare.coss.api.course.dto.EnrollmentSummaryResponse;
import com.cloudsquare.coss.api.course.service.CourseService;

@RestController
@RequestMapping("/api/student")
public class StudentCourseController {

    private final CourseService courseService;

    public StudentCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public List<CourseSummaryResponse> getCourses() {
        return courseService.getAvailableCourses();
    }

    @GetMapping("/enrollments")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public List<EnrollmentSummaryResponse> getMyEnrollments(Authentication authentication) {
        return courseService.getMyEnrollments(authentication.getName());
    }
}
