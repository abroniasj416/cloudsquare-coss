package com.cloudsquare.coss.api.lecture.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudsquare.coss.api.lecture.entity.LectureVideo;
import com.cloudsquare.coss.api.lecture.entity.LectureVideoStatus;
import com.cloudsquare.coss.api.lecture.repository.LectureVideoRepository;
import com.cloudsquare.coss.api.lecture.storage.ObjectStorageService;

@Service
public class ThumbnailService {

    private final LectureVideoRepository lectureVideoRepository;
    private final ObjectStorageService objectStorageService;
    private final FfmpegExecutor ffmpegExecutor;

    public ThumbnailService(
            LectureVideoRepository lectureVideoRepository,
            ObjectStorageService objectStorageService,
            FfmpegExecutor ffmpegExecutor
    ) {
        this.lectureVideoRepository = lectureVideoRepository;
        this.objectStorageService = objectStorageService;
        this.ffmpegExecutor = ffmpegExecutor;
    }

    @Async
    @Transactional
    public void generateThumbnailAsync(Long lectureVideoId) {
        LectureVideo video = lectureVideoRepository.findById(lectureVideoId).orElse(null);
        if (video == null) {
            return;
        }

        Path tempDir = null;
        try {
            video.setStatus(LectureVideoStatus.PROCESSING);
            lectureVideoRepository.save(video);

            tempDir = Files.createTempDirectory("lms-thumb-" + lectureVideoId + "-");
            Path input = tempDir.resolve("video.mp4");
            Path output = tempDir.resolve("thumbnail.jpg");

            objectStorageService.downloadToFile(video.getVideoKey(), input);
            ffmpegExecutor.captureFrame(input, output, 2);

            String thumbnailKey = toThumbnailObjectKey(video.getVideoKey());
            objectStorageService.uploadFile(thumbnailKey, output, "image/jpeg");

            video.setThumbnailKey(thumbnailKey);
            video.setStatus(LectureVideoStatus.READY);
            lectureVideoRepository.save(video);
        } catch (Exception e) {
            video.setStatus(LectureVideoStatus.UPLOADED);
            lectureVideoRepository.save(video);
        } finally {
            cleanup(tempDir);
        }
    }

    private String toThumbnailObjectKey(String videoKey) {
        int dotIndex = videoKey.lastIndexOf('.');
        if (dotIndex < 0) {
            return videoKey + ".jpg";
        }
        return videoKey.substring(0, dotIndex) + ".jpg";
    }

    private void cleanup(Path tempDir) {
        if (tempDir == null) {
            return;
        }

        try {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
