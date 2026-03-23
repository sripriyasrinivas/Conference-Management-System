package com.cms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id")
    private Conference conference;

    @Enumerated(EnumType.STRING)
    private CertificateType type;

    private String certificateNumber;
    private String filePath;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();

    public enum CertificateType {
        PARTICIPATION("Certificate of Participation"),
        PRESENTATION("Certificate of Presentation"),
        BEST_PAPER("Best Paper Award"),
        REVIEWER("Certificate of Review");

        private final String title;

        CertificateType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
