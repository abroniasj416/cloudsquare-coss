package com.cloudsquare.coss.api.lecture.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudsquare.coss.api.lecture.dto.UploadInitResponse;
import com.cloudsquare.coss.api.lecture.repository.EnrollmentRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureVideoRepository;
import com.cloudsquare.coss.api.lecture.storage.ObjectStorageProperties;
import com.cloudsquare.coss.api.lecture.storage.ObjectStorageService;

@ExtendWith(MockitoExtension.class)
class PresignedUploadInitTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureVideoRepository lectureVideoRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private ThumbnailService thumbnailService;

    private LectureVideoService lectureVideoService;

    @BeforeEach
    void setUp() {
        ObjectStorageProperties properties = new ObjectStorageProperties();
        properties.setVideoPrefix("videos");
        properties.setPresignedPutExpiresSeconds(600);
        properties.setPresignedGetExpiresSeconds(300);

        lectureVideoService = new LectureVideoService(
                lectureRepository,
                lectureVideoRepository,
                enrollmentRepository,
                properties,
                objectStorageService,
                thumbnailService);
    }

    @Test
    void createUploadInitGeneratesExpectedObjectKeyAndPresignedUrl() {
        Long lectureId = 77L;
        when(lectureRepository.existsById(lectureId)).thenReturn(true);
        when(objectStorageService.generatePresignedPutUrl(any(), eq(Duration.ofSeconds(600)))).thenReturn("https://put.example");

        UploadInitResponse response = lectureVideoService.createUploadInit(lectureId);

        assertTrue(response.objectKey().startsWith("videos/lectures/77/"));
        assertTrue(response.objectKey().endsWith(".mp4"));
        assertEquals("https://put.example", response.presignedPutUrl());
        assertEquals(600, response.expiresInSeconds());

        verify(objectStorageService).generatePresignedPutUrl(any(), eq(Duration.ofSeconds(600)));
    }
}
