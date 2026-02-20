package com.cloudsquare.coss.api.lecture.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudsquare.coss.api.lecture.dto.CompletionCertificateResponse;
import com.cloudsquare.coss.api.lecture.service.CompletionCertificateService;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CompletionCertificateService completionCertificateService;

    public CertificateController(CompletionCertificateService completionCertificateService) {
        this.completionCertificateService = completionCertificateService;
    }

    @GetMapping("/me")
    public List<CompletionCertificateResponse> getMyCertificates(Authentication authentication) {
        return completionCertificateService.getMyCertificates(authentication.getName());
    }

    @GetMapping
    public List<CompletionCertificateResponse> getCertificatesByUserId(@RequestParam Long userId) {
        // Demo only: external integration endpoint assumes internal network access controls.
        return completionCertificateService.getCertificatesByUserId(userId);
    }

    @GetMapping("/lectures/{lectureId}/me")
    public CompletionCertificateResponse getMyCertificateByLecture(
            @PathVariable Long lectureId,
            Authentication authentication
    ) {
        return completionCertificateService.getMyCertificateByLecture(lectureId, authentication.getName());
    }
}
