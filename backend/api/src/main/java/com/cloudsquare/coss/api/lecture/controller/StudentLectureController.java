package com.cloudsquare.coss.api.lecture.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudsquare.coss.api.lecture.dto.CompletionCertificateResponse;
import com.cloudsquare.coss.api.lecture.dto.EnrollmentResponse;
import com.cloudsquare.coss.api.lecture.dto.LectureDetailResponse;
import com.cloudsquare.coss.api.lecture.dto.LectureSummaryResponse;
import com.cloudsquare.coss.api.lecture.dto.PlaybackResponse;
import com.cloudsquare.coss.api.lecture.service.CompletionCertificateService;
import com.cloudsquare.coss.api.lecture.service.LectureService;
import com.cloudsquare.coss.api.lecture.service.LectureVideoService;

@RestController
@RequestMapping("/api/lectures")
public class StudentLectureController {

    private final LectureService lectureService;
    private final LectureVideoService lectureVideoService;
    private final CompletionCertificateService completionCertificateService;

    public StudentLectureController(
            LectureService lectureService,
            LectureVideoService lectureVideoService,
            CompletionCertificateService completionCertificateService
    ) {
        this.lectureService = lectureService;
        this.lectureVideoService = lectureVideoService;
        this.completionCertificateService = completionCertificateService;
    }

    @GetMapping
    public List<LectureSummaryResponse> getLectures() {
        return lectureService.getLectures();
    }

    @GetMapping("/{lectureId}")
    public LectureDetailResponse getLecture(@PathVariable Long lectureId) {
        return lectureService.getLecture(lectureId);
    }

    @PostMapping("/{lectureId}/enroll")
    public EnrollmentResponse enroll(@PathVariable Long lectureId, Authentication authentication) {
        return lectureService.enroll(lectureId, authentication.getName());
    }

    @PostMapping("/{lectureId}/complete")
    public CompletionCertificateResponse complete(@PathVariable Long lectureId, Authentication authentication) {
        return completionCertificateService.completeLecture(lectureId, authentication.getName());
    }

    @GetMapping("/{lectureId}/playback")
    public PlaybackResponse playback(@PathVariable Long lectureId, Authentication authentication) {
        return lectureVideoService.getPlayback(lectureId, authentication.getName());
    }
}
