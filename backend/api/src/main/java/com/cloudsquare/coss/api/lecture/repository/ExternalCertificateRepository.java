package com.cloudsquare.coss.api.lecture.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.cloudsquare.coss.api.lecture.dto.CompletionCertificateResponse;

@Repository
public class ExternalCertificateRepository {

    public List<CompletionCertificateResponse> findAllForDemo() {
        return List.of(
                new CompletionCertificateResponse("0001-0002", 1L, 2L, Instant.parse("2026-01-01T10:00:00Z")),
                new CompletionCertificateResponse("0002-0004", 2L, 4L, Instant.parse("2026-01-03T11:30:00Z")),
                new CompletionCertificateResponse("0003-0006", 3L, 6L, Instant.parse("2026-01-05T09:15:00Z"))
        );
    }
}
