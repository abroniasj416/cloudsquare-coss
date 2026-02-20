package com.cloudsquare.coss.api.lecture.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cloudsquare.coss.api.lecture.dto.CompletionCertificateResponse;
import com.cloudsquare.coss.api.lecture.entity.CompletionCertificate;
import com.cloudsquare.coss.api.lecture.repository.CompletionCertificateRepository;
import com.cloudsquare.coss.api.lecture.repository.LectureRepository;

@Service
public class CompletionCertificateService {

    private final CompletionCertificateRepository completionCertificateRepository;
    private final LectureRepository lectureRepository;

    public CompletionCertificateService(
            CompletionCertificateRepository completionCertificateRepository,
            LectureRepository lectureRepository
    ) {
        this.completionCertificateRepository = completionCertificateRepository;
        this.lectureRepository = lectureRepository;
    }

    @Transactional
    public CompletionCertificateResponse completeLecture(Long lectureId, String principalName) {
        Long studentId = toDemoStudentId(principalName);

        return completionCertificateRepository.findByLectureIdAndUserId(lectureId, studentId)
                .map(this::toResponse)
                .orElseGet(() -> issueCertificate(lectureId, studentId));
    }

    @Transactional(readOnly = true)
    public List<CompletionCertificateResponse> getMyCertificates(String principalName) {
        Long studentId = toDemoStudentId(principalName);
        return completionCertificateRepository.findByUserIdOrderByIssuedAtDesc(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompletionCertificateResponse> getCertificatesByUserId(Long userId) {
        return completionCertificateRepository.findByUserIdOrderByIssuedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompletionCertificateResponse getMyCertificateByLecture(Long lectureId, String principalName) {
        Long studentId = toDemoStudentId(principalName);
        return completionCertificateRepository.findByLectureIdAndUserIdOrderByIssuedAtDesc(lectureId, studentId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "certificate not found"));
    }

    public static String formatSerialNumber(Long lectureId, Long studentId) {
        return "%04d-%04d".formatted(lectureId, studentId);
    }

    private CompletionCertificateResponse issueCertificate(Long lectureId, Long studentId) {
        if (!lectureRepository.existsById(lectureId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "lecture not found");
        }

        CompletionCertificate certificate = new CompletionCertificate();
        certificate.setLectureId(lectureId);
        certificate.setUserId(studentId);
        certificate.setSerialNumber(formatSerialNumber(lectureId, studentId));

        try {
            CompletionCertificate saved = completionCertificateRepository.save(certificate);
            return toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            return completionCertificateRepository.findByLectureIdAndUserId(lectureId, studentId)
                    .map(this::toResponse)
                    .orElseThrow(() -> ex);
        }
    }

    private CompletionCertificateResponse toResponse(CompletionCertificate certificate) {
        return new CompletionCertificateResponse(
                certificate.getSerialNumber(),
                certificate.getLectureId(),
                certificate.getUserId(),
                certificate.getIssuedAt());
    }

    private Long toDemoStudentId(String principalName) {
        if (principalName == null || principalName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid user");
        }

        String digits = principalName.replaceAll("\\D", "");
        if (!digits.isBlank()) {
            try {
                return Long.parseLong(digits);
            } catch (NumberFormatException ignored) {
                // Fallback below
            }
        }

        return (long) (Math.floorMod(principalName.hashCode(), 10_000) + 1);
    }
}
