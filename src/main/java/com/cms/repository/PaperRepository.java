package com.cms.repository;

import com.cms.model.Paper;
import com.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByAuthor(User author);
    List<Paper> findByStatus(Paper.Status status);
    List<Paper> findByAuthorId(Long authorId);

    @Query("SELECT p FROM Paper p WHERE p.status NOT IN ('REJECTED')")
    List<Paper> findActivePapers();

    @Query("SELECT p FROM Paper p WHERE p.author = :author ORDER BY p.submittedAt DESC")
    List<Paper> findByAuthorOrderBySubmittedAtDesc(User author);

    long countByStatus(Paper.Status status);
}
