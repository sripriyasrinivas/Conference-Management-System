package com.cms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String venue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate submissionDeadline;
    private LocalDate reviewDeadline;
    private LocalDate notificationDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ConferenceStatus status = ConferenceStatus.UPCOMING;

    private int maxAttendees;
    private String websiteUrl;
    private String contactEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Registration> registrations = new ArrayList<>();

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ConferenceStatus {
        UPCOMING, OPEN_FOR_SUBMISSIONS, UNDER_REVIEW, SCHEDULE_PUBLISHED, ONGOING, COMPLETED, CANCELLED
    }
}
