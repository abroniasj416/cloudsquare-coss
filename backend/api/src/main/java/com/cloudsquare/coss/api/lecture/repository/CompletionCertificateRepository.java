package com.cloudsquare.coss.api.lecture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudsquare.coss.api.lecture.entity.CompletionCertificate;

public interface CompletionCertificateRepository extends JpaRepository<CompletionCertificate, Long> {

    Optional<CompletionCertificate> findByLectureIdAndUserId(Long lectureId, Long userId);

    List<CompletionCertificate> findByUserIdOrderByIssuedAtDesc(Long userId);

    Optional<CompletionCertificate> findByLectureIdAndUserIdOrderByIssuedAtDesc(Long lectureId, Long userId);
}
