package com.cms.service.impl;

import com.cms.model.*;
import com.cms.repository.CertificateRepository;
import com.cms.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateServiceImpl {

    private final CertificateRepository certificateRepository;
    private final RegistrationRepository registrationRepository;

    public Certificate issueCertificate(User recipient, Conference conference,
                                         Certificate.CertificateType type) {
        if (certificateRepository.existsByRecipientAndConferenceAndType(recipient, conference, type)) {
            throw new RuntimeException("Certificate already issued.");
        }

        String certNumber = "CMS-" + conference.getId() + "-" +
                type.name().substring(0, 2) + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Certificate cert = Certificate.builder()
                .recipient(recipient)
                .conference(conference)
                .type(type)
                .certificateNumber(certNumber)
                .build();

        return certificateRepository.save(cert);
    }

    public void bulkIssueParticipationCertificates(Conference conference) {
        List<Registration> confirmed = registrationRepository.findByConference(conference)
                .stream()
                .filter(r -> r.getStatus() == Registration.RegistrationStatus.CONFIRMED)
                .toList();

        for (Registration reg : confirmed) {
            try {
                issueCertificate(reg.getAttendee(), conference, Certificate.CertificateType.PARTICIPATION);
                reg.setCertificateIssued(true);
                registrationRepository.save(reg);
            } catch (RuntimeException ignored) {
                // Already issued
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Certificate> findByRecipient(User recipient) {
        return certificateRepository.findByRecipient(recipient);
    }

    @Transactional(readOnly = true)
    public List<Certificate> findByConference(Conference conference) {
        return certificateRepository.findByConference(conference);
    }

    @Transactional(readOnly = true)
    public List<Certificate> findAll() {
        return certificateRepository.findAll();
    }
}
