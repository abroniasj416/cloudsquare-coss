package com.cloudsquare.coss.api.lecture.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudsquare.coss.api.lecture.dto.CompletionCertificateResponse;
import com.cloudsquare.coss.api.lecture.repository.ExternalCertificateRepository;

@Service
public class ExternalCertificateService {

    private final ExternalCertificateRepository externalCertificateRepository;

    public ExternalCertificateService(ExternalCertificateRepository externalCertificateRepository) {
        this.externalCertificateRepository = externalCertificateRepository;
    }

    @Transactional(readOnly = true)
    public List<CompletionCertificateResponse> getSampleCertificates() {
        return externalCertificateRepository.findAllForDemo();
    }
}
