package com.cloudsquare.coss.api.lecture.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudsquare.coss.api.lecture.entity.LectureVideo;
import com.cloudsquare.coss.api.lecture.entity.LectureVideoStatus;
import com.cloudsquare.coss.api.lecture.repository.LectureVideoRepository;
import com.cloudsquare.coss.api.lecture.storage.ObjectStorageService;

@ExtendWith(MockitoExtension.class)
class ThumbnailStatusTransitionTest {

    @Mock
    private LectureVideoRepository lectureVideoRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private FfmpegExecutor ffmpegExecutor;

    @Test
    void thumbnailGenerationTransitionsStatusToReady() {
        LectureVideo video = new LectureVideo();
        video.setVideoKey("videos/lectures/5/sample.mp4");
        video.setStatus(LectureVideoStatus.UPLOADED);

        List<LectureVideoStatus> savedStatuses = new ArrayList<>();

        when(lectureVideoRepository.findById(5L)).thenReturn(Optional.of(video));
        when(lectureVideoRepository.save(any(LectureVideo.class))).thenAnswer(invocation -> {
            LectureVideo saved = invocation.getArgument(0);
            savedStatuses.add(saved.getStatus());
            return saved;
        });

        ThumbnailService thumbnailService = new ThumbnailService(lectureVideoRepository, objectStorageService, ffmpegExecutor);
        thumbnailService.generateThumbnailAsync(5L);

        assertEquals(LectureVideoStatus.READY, video.getStatus());
        assertEquals("videos/lectures/5/sample.jpg", video.getThumbnailKey());
        assertTrue(savedStatuses.contains(LectureVideoStatus.PROCESSING));
        assertTrue(savedStatuses.contains(LectureVideoStatus.READY));

        verify(objectStorageService).downloadToFile(any(), any());
        verify(objectStorageService).uploadFile(any(), any(), any());
    }
}