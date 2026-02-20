package com.cloudsquare.coss.api.lecture.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CompletionCertificateServiceTest {

    @Test
    void formatSerialNumber_shouldUseZeroPadding() {
        String serial = CompletionCertificateService.formatSerialNumber(1L, 2L);

        assertEquals("0001-0002", serial);
    }
}
