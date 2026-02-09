package com.cloudsquare.coss.api.lecture.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cloudsquare.coss.api.lecture.dto.CreateLectureRequest;
import com.cloudsquare.coss.api.lecture.dto.EnrollmentResponse;
import com.cloudsquare.coss.api.lecture.dto.LectureDetailResponse;
import com.cloudsquare.coss.api.lecture.dto.LectureSummaryResponse;
import com.cloudsquare.coss.api.lecture.entity.Enrollment;
import com.cloudsquare.coss.api.lecture.entity.Lecture;
import com.cloudsquare.coss.api.lecture.entity.LectureVideo;
import com.cloudsquare.coss.api.lecture.repository.EnrollmentRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureVideoRepository;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureVideoRepository lectureVideoRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureVideoService lectureVideoService;

    public LectureService(
            LectureRepository lectureRepository,
            LectureVideoRepository lectureVideoRepository,
            EnrollmentRepository enrollmentRepository,
            LectureVideoService lectureVideoService
    ) {
        this.lectureRepository = lectureRepository;
        this.lectureVideoRepository = lectureVideoRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lectureVideoService = lectureVideoService;
    }

    @Transactional
    public LectureDetailResponse createLecture(CreateLectureRequest request) {
        validateCreateLectureRequest(request);

        Lecture lecture = new Lecture();
        lecture.setTitle(request.title().trim());
        lecture.setDescription(request.description().trim());
        Lecture saved = lectureRepository.save(lecture);

        return new LectureDetailResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                null,
                null,
                saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<LectureSummaryResponse> getLectures() {
        return lectureRepository.findAll().stream()
                .map(lecture -> {
                    LectureVideo latest = lectureVideoRepository.findTopByLecture_IdOrderByCreatedAtDesc(lecture.getId()).orElse(null);
                    return new LectureSummaryResponse(
                            lecture.getId(),
                            lecture.getTitle(),
                            lecture.getDescription(),
                            lectureVideoService.resolveThumbnailUrl(latest),
                            latest != null ? latest.getStatus().name() : null,
                            lecture.getCreatedAt());
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public LectureDetailResponse getLecture(Long lectureId) {
        Lecture lecture = getLectureOrThrow(lectureId);
        LectureVideo latest = lectureVideoRepository.findTopByLecture_IdOrderByCreatedAtDesc(lectureId).orElse(null);

        return new LectureDetailResponse(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getDescription(),
                lectureVideoService.resolveThumbnailUrl(latest),
                latest != null ? latest.getStatus().name() : null,
                lecture.getCreatedAt());
    }

    @Transactional
    public EnrollmentResponse enroll(Long lectureId, String userId) {
        Lecture lecture = getLectureOrThrow(lectureId);

        return enrollmentRepository.findByLecture_IdAndUserId(lectureId, userId)
                .map(existing -> new EnrollmentResponse(existing.getId(), lectureId, existing.getUserId(), existing.getCreatedAt()))
                .orElseGet(() -> {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setLecture(lecture);
                    enrollment.setUserId(userId);
                    Enrollment saved = enrollmentRepository.save(enrollment);
                    return new EnrollmentResponse(saved.getId(), lectureId, saved.getUserId(), saved.getCreatedAt());
                });
    }

    private Lecture getLectureOrThrow(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lecture not found"));
    }

    private void validateCreateLectureRequest(CreateLectureRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is required");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }
        if (request.description() == null || request.description().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "description is required");
        }
    }
}
