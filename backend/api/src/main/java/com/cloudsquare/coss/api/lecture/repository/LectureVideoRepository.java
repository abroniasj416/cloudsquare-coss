package com.cloudsquare.coss.api.lecture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudsquare.coss.api.lecture.entity.LectureVideo;
import com.cloudsquare.coss.api.lecture.entity.LectureVideoStatus;

public interface LectureVideoRepository extends JpaRepository<LectureVideo, Long> {

    Optional<LectureVideo> findTopByLecture_IdOrderByCreatedAtDesc(Long lectureId);

    Optional<LectureVideo> findTopByLecture_IdAndStatusOrderByUpdatedAtDesc(Long lectureId, LectureVideoStatus status);
}
