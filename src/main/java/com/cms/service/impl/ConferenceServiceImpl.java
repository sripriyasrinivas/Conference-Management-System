package com.cms.service.impl;

import com.cms.model.Conference;
import com.cms.repository.ConferenceRepository;
import com.cms.service.ConferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConferenceServiceImpl implements ConferenceService {

    private final ConferenceRepository conferenceRepository;

    @Override
    public Conference create(Conference conference) {
        return conferenceRepository.save(conference);
    }

    @Override
    public Conference update(Long id, Conference updated) {
        Conference existing = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found: " + id));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setVenue(updated.getVenue());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setSubmissionDeadline(updated.getSubmissionDeadline());
        existing.setReviewDeadline(updated.getReviewDeadline());
        existing.setNotificationDate(updated.getNotificationDate());
        existing.setMaxAttendees(updated.getMaxAttendees());
        existing.setWebsiteUrl(updated.getWebsiteUrl());
        existing.setContactEmail(updated.getContactEmail());
        return conferenceRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conference> findById(Long id) {
        return conferenceRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conference> findAll() {
        return conferenceRepository.findByOrderByStartDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conference> findByStatus(Conference.ConferenceStatus status) {
        return conferenceRepository.findByStatus(status);
    }

    @Override
    public Conference updateStatus(Long id, Conference.ConferenceStatus status) {
        Conference c = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found: " + id));
        c.setStatus(status);
        return conferenceRepository.save(c);
    }

    @Override
    public void delete(Long id) {
        conferenceRepository.deleteById(id);
    }
}
