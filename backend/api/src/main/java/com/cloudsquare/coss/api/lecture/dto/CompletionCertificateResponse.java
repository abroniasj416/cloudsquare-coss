package com.cloudsquare.coss.api.lecture.dto;

import java.time.Instant;

public record CompletionCertificateResponse(
        String serialNumber,
        Long lectureId,
        Long userId,
        Instant issuedAt
) {
}
