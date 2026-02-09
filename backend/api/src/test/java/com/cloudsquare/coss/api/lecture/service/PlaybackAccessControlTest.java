package com.cloudsquare.coss.api.lecture.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.cloudsquare.coss.api.lecture.repository.EnrollmentRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureVideoRepository;
import com.cloudsquare.coss.api.lecture.storage.ObjectStorageProperties;
import com.cloudsquare.coss.api.lecture.storage.ObjectStorageService;

@ExtendWith(MockitoExtension.class)
class PlaybackAccessControlTest {

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
    void playbackDeniedWhenUserIsNotEnrolled() {
        Long lectureId = 10L;
        String userId = "student1";

        when(lectureRepository.existsById(lectureId)).thenReturn(true);
        when(enrollmentRepository.existsByLecture_IdAndUserId(lectureId, userId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> lectureVideoService.getPlayback(lectureId, userId));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }
}
