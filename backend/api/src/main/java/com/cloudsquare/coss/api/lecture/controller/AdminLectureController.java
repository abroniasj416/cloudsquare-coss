package com.cloudsquare.coss.api.lecture.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cloudsquare.coss.api.lecture.dto.CreateLectureRequest;
import com.cloudsquare.coss.api.lecture.dto.LectureDetailResponse;
import com.cloudsquare.coss.api.lecture.dto.UploadCompleteRequest;
import com.cloudsquare.coss.api.lecture.dto.UploadCompleteResponse;
import com.cloudsquare.coss.api.lecture.dto.UploadInitResponse;
import com.cloudsquare.coss.api.lecture.service.LectureService;
import com.cloudsquare.coss.api.lecture.service.LectureVideoService;

@RestController
@RequestMapping("/api/admin/lectures")
public class AdminLectureController {

    private final LectureService lectureService;
    private final LectureVideoService lectureVideoService;

    public AdminLectureController(LectureService lectureService, LectureVideoService lectureVideoService) {
        this.lectureService = lectureService;
        this.lectureVideoService = lectureVideoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public LectureDetailResponse createLecture(@RequestBody CreateLectureRequest request) {
        return lectureService.createLecture(request);
    }

    @PostMapping("/{lectureId}/video/upload-init")
    @PreAuthorize("hasRole('ADMIN')")
    public UploadInitResponse uploadInit(@PathVariable Long lectureId) {
        return lectureVideoService.createUploadInit(lectureId);
    }

    @PostMapping("/{lectureId}/video/upload-complete")
    @PreAuthorize("hasRole('ADMIN')")
    public UploadCompleteResponse uploadComplete(
            @PathVariable Long lectureId,
            @RequestBody UploadCompleteRequest request
    ) {
        return lectureVideoService.completeUpload(lectureId, request);
    }
}
