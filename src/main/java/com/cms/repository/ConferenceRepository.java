package com.cms.repository;

import com.cms.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    List<Conference> findByStatus(Conference.ConferenceStatus status);
    List<Conference> findByOrderByStartDateDesc();
}
