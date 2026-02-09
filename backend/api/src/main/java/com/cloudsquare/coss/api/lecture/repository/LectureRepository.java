package com.cloudsquare.coss.api.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudsquare.coss.api.lecture.entity.Lecture;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
}
