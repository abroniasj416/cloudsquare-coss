package com.cloudsquare.coss.api.lecture.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cloudsquare.coss.api.lecture.dto.PlaybackResponse;
import com.cloudsquare.coss.api.lecture.dto.UploadCompleteRequest;
import com.cloudsquare.coss.api.lecture.dto.UploadCompleteResponse;
import com.cloudsquare.coss.api.lecture.dto.UploadInitResponse;
import com.cloudsquare.coss.api.lecture.entity.Lecture;
import com.cloudsquare.coss.api.lecture.entity.LectureVideo;
import com.cloudsquare.coss.api.lecture.entity.LectureVideoStatus;
import com.cloudsquare.coss.api.lecture.repository.EnrollmentRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureVideoRepository;
import com.cloudsquare.coss.api.lecture.storage.ObjectStorageProperties;
import com.cloudsquare.coss.api.lecture.storage.ObjectStorageService;

@Service
public class LectureVideoService {

    private final LectureRepository lectureRepository;
    private final LectureVideoRepository lectureVideoRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ObjectStorageProperties storageProperties;
    private final ObjectStorageService objectStorageService;
    private final ThumbnailService thumbnailService;

    public LectureVideoService(
            LectureRepository lectureRepository,
            LectureVideoRepository lectureVideoRepository,
            EnrollmentRepository enrollmentRepository,
            ObjectStorageProperties storageProperties,
            ObjectStorageService objectStorageService,
            ThumbnailService thumbnailService
    ) {
        this.lectureRepository = lectureRepository;
        this.lectureVideoRepository = lectureVideoRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.storageProperties = storageProperties;
        this.objectStorageService = objectStorageService;
        this.thumbnailService = thumbnailService;
    }

    @Transactional(readOnly = true)
    public UploadInitResponse createUploadInit(Long lectureId) {
        assertLectureExists(lectureId);

        String objectKey = buildVideoObjectKey(lectureId);
        String presignedPutUrl = objectStorageService.generatePresignedPutUrl(
                objectKey,
                Duration.ofSeconds(storageProperties.getPresignedPutExpiresSeconds()));

        return new UploadInitResponse(
                objectKey,
                presignedPutUrl,
                storageProperties.getPresignedPutExpiresSeconds());
    }

    @Transactional
    public UploadCompleteResponse completeUpload(Long lectureId, UploadCompleteRequest request) {
        Lecture lecture = getLectureOrThrow(lectureId);
        validateUploadCompleteRequest(request);

        if (!objectStorageService.objectExists(request.objectKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "uploaded object not found in storage");
        }

        LectureVideo lectureVideo = new LectureVideo();
        lectureVideo.setLecture(lecture);
        lectureVideo.setVideoKey(request.objectKey());
        lectureVideo.setSizeBytes(request.sizeBytes());
        lectureVideo.setContentType(request.contentType());
        lectureVideo.setStatus(LectureVideoStatus.UPLOADED);
        lectureVideo = lectureVideoRepository.save(lectureVideo);

        thumbnailService.generateThumbnailAsync(lectureVideo.getId());

        return new UploadCompleteResponse(lectureVideo.getId(), lectureVideo.getStatus().name());
    }

    @Transactional(readOnly = true)
    public PlaybackResponse getPlayback(Long lectureId, String userId) {
        assertLectureExists(lectureId);

        if (!enrollmentRepository.existsByLecture_IdAndUserId(lectureId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "enrollment required");
        }

        LectureVideo video = lectureVideoRepository
                .findTopByLecture_IdAndStatusOrderByUpdatedAtDesc(lectureId, LectureVideoStatus.READY)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ready video not found"));

        String playbackUrl = buildPlaybackUrl(video.getVideoKey());
        return new PlaybackResponse(playbackUrl, storageProperties.getPresignedGetExpiresSeconds());
    }

    public String resolveThumbnailUrl(LectureVideo lectureVideo) {
        if (lectureVideo == null || lectureVideo.getThumbnailKey() == null || lectureVideo.getThumbnailKey().isBlank()) {
            return null;
        }
        return buildPlaybackUrl(lectureVideo.getThumbnailKey());
    }

    private String buildPlaybackUrl(String objectKey) {
        String cdnBaseUrl = storageProperties.getCdnBaseUrl();
        if (cdnBaseUrl != null && !cdnBaseUrl.isBlank()) {
            return normalizeBaseUrl(cdnBaseUrl) + "/" + stripLeadingSlash(objectKey);
        }

        return objectStorageService.generatePresignedGetUrl(
                objectKey,
                Duration.ofSeconds(storageProperties.getPresignedGetExpiresSeconds()));
    }

    private String buildVideoObjectKey(Long lectureId) {
        String prefix = stripSlashes(storageProperties.getVideoPrefix());
        String base = prefix.isBlank() ? "lectures" : prefix + "/lectures";
        return base + "/" + lectureId + "/" + UUID.randomUUID() + ".mp4";
    }

    private void validateUploadCompleteRequest(UploadCompleteRequest request) {
        if (request == null || request.objectKey() == null || request.objectKey().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "objectKey is required");
        }
        if (request.sizeBytes() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sizeBytes must be greater than 0");
        }
        if (request.contentType() == null || request.contentType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "contentType is required");
        }
    }

    private void assertLectureExists(Long lectureId) {
        if (!lectureRepository.existsById(lectureId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "lecture not found");
        }
    }

    private Lecture getLectureOrThrow(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lecture not found"));
    }

    private String normalizeBaseUrl(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String stripLeadingSlash(String value) {
        if (value == null) {
            return "";
        }
        return value.startsWith("/") ? value.substring(1) : value;
    }

    private String stripSlashes(String value) {
        if (value == null) {
            return "";
        }
        String stripped = value.trim();
        while (stripped.startsWith("/")) {
            stripped = stripped.substring(1);
        }
        while (stripped.endsWith("/")) {
            stripped = stripped.substring(0, stripped.length() - 1);
        }
        return stripped;
    }
}
