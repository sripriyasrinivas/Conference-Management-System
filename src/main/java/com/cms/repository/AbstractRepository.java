package com.cms.repository;

import com.cms.model.Abstract;
import com.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AbstractRepository extends JpaRepository<Abstract, Long> {
    List<Abstract> findByAuthor(User author);
    List<Abstract> findByStatus(Abstract.AbstractStatus status);
    List<Abstract> findByAuthorId(Long authorId);
}
