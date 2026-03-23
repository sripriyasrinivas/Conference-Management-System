package com.cms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(columnDefinition = "TEXT")
    private String strengthsComments;

    @Column(columnDefinition = "TEXT")
    private String weaknessComments;

    private Integer score; // 1-10

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Recommendation recommendation = Recommendation.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.ASSIGNED;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime assignedAt = LocalDateTime.now();

    private LocalDateTime submittedAt;
    private LocalDateTime deadline;

    public enum Recommendation {
        PENDING, ACCEPT, REJECT, MINOR_REVISION, MAJOR_REVISION
    }

    public enum ReviewStatus {
        ASSIGNED, IN_PROGRESS, SUBMITTED
    }
}
