package com.cms.service;

import com.cms.model.Conference;
import java.util.List;
import java.util.Optional;

public interface ConferenceService {
    Conference create(Conference conference);
    Conference update(Long id, Conference conference);
    Optional<Conference> findById(Long id);
    List<Conference> findAll();
    List<Conference> findByStatus(Conference.ConferenceStatus status);
    Conference updateStatus(Long id, Conference.ConferenceStatus status);
    void delete(Long id);
}
