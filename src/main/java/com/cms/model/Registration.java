package com.cms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private User attendee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private RegistrationType type;

    private String dietaryPreferences;
    private String specialRequirements;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();

    private LocalDateTime confirmedAt;
    private boolean certificateIssued;

    public enum RegistrationStatus {
        PENDING, CONFIRMED, CANCELLED, WAITLISTED
    }

    public enum RegistrationType {
        STUDENT, PROFESSIONAL, ACADEMIC, VIRTUAL
    }
}
