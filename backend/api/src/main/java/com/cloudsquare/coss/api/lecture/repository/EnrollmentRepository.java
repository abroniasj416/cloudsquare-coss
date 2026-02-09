package com.cloudsquare.coss.api.lecture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudsquare.coss.api.lecture.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByLecture_IdAndUserId(Long lectureId, String userId);

    Optional<Enrollment> findByLecture_IdAndUserId(Long lectureId, String userId);
}
