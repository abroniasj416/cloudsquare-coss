package com.cloudsquare.coss.api.course.service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cloudsquare.coss.api.course.dto.CourseDetailResponse;
import com.cloudsquare.coss.api.course.dto.CourseSummaryResponse;
import com.cloudsquare.coss.api.course.dto.CourseUpsertRequest;
import com.cloudsquare.coss.api.course.dto.EnrollmentSummaryResponse;

import jakarta.annotation.PostConstruct;

@Service
public class CourseService {

    private final AtomicLong courseSequence = new AtomicLong(2000L);
    private final AtomicLong enrollmentSequence = new AtomicLong(9000L);

    private final Map<Long, CourseRecord> courses = new ConcurrentHashMap<>();
    private final Map<String, List<EnrollmentRecord>> enrollmentsByStudent = new ConcurrentHashMap<>();

    @PostConstruct
    void initializeSampleData() {
        CourseRecord java = new CourseRecord(nextCourseId(), "COSS-JAVA-101", "Java Basics", "Kim", 40, 12);
        CourseRecord spring = new CourseRecord(nextCourseId(), "COSS-SPR-201", "Spring API", "Lee", 35, 17);
        courses.put(java.courseId(), java);
        courses.put(spring.courseId(), spring);

        enrollmentsByStudent.put("student1", List.of(
                new EnrollmentRecord(nextEnrollmentId(), java.courseId(), "ENROLLED", Instant.now().minusSeconds(604800)),
                new EnrollmentRecord(nextEnrollmentId(), spring.courseId(), "ENROLLED", Instant.now().minusSeconds(172800))
        ));
    }

    public List<CourseSummaryResponse> getAvailableCourses() {
        return courses.values().stream()
                .sorted(Comparator.comparing(CourseRecord::courseId))
                .map(course -> new CourseSummaryResponse(
                        course.courseId(),
                        course.courseCode(),
                        course.title(),
                        course.instructorName(),
                        course.capacity(),
                        course.enrolledCount()))
                .toList();
    }

    public List<EnrollmentSummaryResponse> getMyEnrollments(String studentId) {
        List<EnrollmentRecord> enrollments = enrollmentsByStudent.getOrDefault(studentId, List.of());

        return enrollments.stream()
                .map(enrollment -> {
                    CourseRecord course = courses.get(enrollment.courseId());
                    if (course == null) {
                        return null;
                    }
                    return new EnrollmentSummaryResponse(
                            enrollment.enrollmentId(),
                            course.courseId(),
                            course.courseCode(),
                            course.title(),
                            enrollment.status(),
                            DateTimeFormatter.ISO_INSTANT.format(enrollment.enrolledAt()));
                })
                .filter(item -> item != null)
                .toList();
    }

    public CourseDetailResponse createCourse(CourseUpsertRequest request) {
        validateRequest(request);

        long courseId = nextCourseId();
        CourseRecord course = new CourseRecord(
                courseId,
                request.courseCode().trim(),
                request.title().trim(),
                request.instructorName().trim(),
                request.capacity(),
                0);

        courses.put(courseId, course);
        return toDetailResponse(course);
    }

    public CourseDetailResponse updateCourse(Long courseId, CourseUpsertRequest request) {
        validateRequest(request);

        CourseRecord current = courses.get(courseId);
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "course not found");
        }

        CourseRecord updated = new CourseRecord(
                current.courseId(),
                request.courseCode().trim(),
                request.title().trim(),
                request.instructorName().trim(),
                request.capacity(),
                current.enrolledCount());

        courses.put(courseId, updated);
        return toDetailResponse(updated);
    }

    public void deleteCourse(Long courseId) {
        CourseRecord removed = courses.remove(courseId);
        if (removed == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "course not found");
        }

        for (Map.Entry<String, List<EnrollmentRecord>> entry : enrollmentsByStudent.entrySet()) {
            List<EnrollmentRecord> filtered = entry.getValue().stream()
                    .filter(enrollment -> !enrollment.courseId().equals(courseId))
                    .toList();
            enrollmentsByStudent.put(entry.getKey(), new ArrayList<>(filtered));
        }
    }

    private CourseDetailResponse toDetailResponse(CourseRecord course) {
        return new CourseDetailResponse(
                course.courseId(),
                course.courseCode(),
                course.title(),
                course.instructorName(),
                course.capacity(),
                course.enrolledCount());
    }

    private void validateRequest(CourseUpsertRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is required");
        }

        if (isBlank(request.courseCode()) || isBlank(request.title()) || isBlank(request.instructorName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courseCode, title, instructorName are required");
        }

        if (request.capacity() == null || request.capacity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "capacity must be greater than 0");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private long nextCourseId() {
        return courseSequence.incrementAndGet();
    }

    private long nextEnrollmentId() {
        return enrollmentSequence.incrementAndGet();
    }

    private record CourseRecord(
            Long courseId,
            String courseCode,
            String title,
            String instructorName,
            Integer capacity,
            Integer enrolledCount
    ) {
    }

    private record EnrollmentRecord(
            Long enrollmentId,
            Long courseId,
            String status,
            Instant enrolledAt
    ) {
    }
}
