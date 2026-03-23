package com.cms.repository;

import com.cms.model.Certificate;
import com.cms.model.User;
import com.cms.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByRecipient(User recipient);
    List<Certificate> findByConference(Conference conference);
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    boolean existsByRecipientAndConferenceAndType(User recipient, Conference conference,
                                                   Certificate.CertificateType type);
}
