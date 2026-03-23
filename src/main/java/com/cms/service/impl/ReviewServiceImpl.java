package com.cms.service.impl;

import com.cms.model.Paper;
import com.cms.model.Review;
import com.cms.model.User;
import com.cms.repository.PaperRepository;
import com.cms.repository.ReviewRepository;
import com.cms.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PaperRepository paperRepository;

    @Override
    public Review assignReview(Paper paper, User reviewer) {
        if (reviewRepository.existsByPaperAndReviewer(paper, reviewer)) {
            throw new RuntimeException("Reviewer already assigned to this paper.");
        }
        if (paper.getAuthor().getId().equals(reviewer.getId())) {
            throw new RuntimeException("Author cannot review their own paper.");
        }

        Review review = Review.builder()
                .paper(paper)
                .reviewer(reviewer)
                .status(Review.ReviewStatus.ASSIGNED)
                .deadline(LocalDateTime.now().plusDays(14).toLocalDate().atStartOfDay())
                .build();

        paper.setStatus(Paper.Status.UNDER_REVIEW);
        paperRepository.save(paper);

        return reviewRepository.save(review);
    }

    @Override
    public Review submitReview(Long reviewId, Review reviewData) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found: " + reviewId));

        review.setComments(reviewData.getComments());
        review.setStrengthsComments(reviewData.getStrengthsComments());
        review.setWeaknessComments(reviewData.getWeaknessComments());
        review.setScore(reviewData.getScore());
        review.setRecommendation(reviewData.getRecommendation());
        review.setStatus(Review.ReviewStatus.SUBMITTED);
        review.setSubmittedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);

        // Auto-decide paper status if all reviews are submitted
        autoDecidePaperStatus(review.getPaper());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByReviewer(User reviewer) {
        return reviewRepository.findByReviewer(reviewer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByPaper(Paper paper) {
        return reviewRepository.findByPaper(paper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findPendingByReviewer(User reviewer) {
        return reviewRepository.findPendingReviewsByReviewer(reviewer);
    }

    @Override
    public void autoDecidePaperStatus(Paper paper) {
        List<Review> reviews = reviewRepository.findByPaper(paper);
        long submitted = reviews.stream()
                .filter(r -> r.getStatus() == Review.ReviewStatus.SUBMITTED)
                .count();

        if (submitted == reviews.size() && !reviews.isEmpty()) {
            long accepts = reviews.stream()
                    .filter(r -> r.getRecommendation() == Review.Recommendation.ACCEPT)
                    .count();
            long rejects = reviews.stream()
                    .filter(r -> r.getRecommendation() == Review.Recommendation.REJECT)
                    .count();
            long minors = reviews.stream()
                    .filter(r -> r.getRecommendation() == Review.Recommendation.MINOR_REVISION)
                    .count();

            Paper.Status newStatus;
            if (accepts > rejects + minors) {
                newStatus = Paper.Status.ACCEPTED;
            } else if (rejects > accepts) {
                newStatus = Paper.Status.REJECTED;
            } else {
                newStatus = Paper.Status.REVISION_REQUIRED;
            }

            paper.setStatus(newStatus);
            paperRepository.save(paper);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasReviewed(Paper paper, User reviewer) {
        return reviewRepository.existsByPaperAndReviewer(paper, reviewer);
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageScore(Paper paper) {
        Double avg = reviewRepository.getAverageScoreForPaper(paper);
        return avg != null ? avg : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(Review.ReviewStatus status) {
        return reviewRepository.countByStatus(status);
    }
}
