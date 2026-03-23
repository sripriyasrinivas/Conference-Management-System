package com.cms.repository;

import com.cms.model.Review;
import com.cms.model.User;
import com.cms.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewer(User reviewer);
    List<Review> findByPaper(Paper paper);
    List<Review> findByReviewerId(Long reviewerId);
    Optional<Review> findByPaperAndReviewer(Paper paper, User reviewer);
    boolean existsByPaperAndReviewer(Paper paper, User reviewer);

    @Query("SELECT r FROM Review r WHERE r.reviewer = :reviewer AND r.status != 'SUBMITTED'")
    List<Review> findPendingReviewsByReviewer(User reviewer);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewer = :reviewer AND r.status = 'SUBMITTED'")
    long countCompletedReviewsByReviewer(User reviewer);

    @Query("SELECT AVG(r.score) FROM Review r WHERE r.paper = :paper AND r.score IS NOT NULL")
    Double getAverageScoreForPaper(Paper paper);

    long countByStatus(Review.ReviewStatus status);
}
