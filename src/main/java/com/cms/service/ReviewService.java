package com.cms.service;

import com.cms.model.Review;
import com.cms.model.Paper;
import com.cms.model.User;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review assignReview(Paper paper, User reviewer);
    Review submitReview(Long reviewId, Review reviewData);
    Optional<Review> findById(Long id);
    List<Review> findByReviewer(User reviewer);
    List<Review> findByPaper(Paper paper);
    List<Review> findPendingByReviewer(User reviewer);
    void autoDecidePaperStatus(Paper paper);
    boolean hasReviewed(Paper paper, User reviewer);
    double getAverageScore(Paper paper);
    long countByStatus(Review.ReviewStatus status);
}
