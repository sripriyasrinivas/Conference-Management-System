package com.cms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "abstracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Abstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String keywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AbstractStatus status = AbstractStatus.SUBMITTED;

    private String reviewComments;
    private int version = 1;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public enum AbstractStatus {
        SUBMITTED, APPROVED, REJECTED, REVISION_REQUESTED
    }
}
