package com.cms.service.impl;

import com.cms.model.Session;
import com.cms.model.User;
import com.cms.repository.SessionRepository;
import com.cms.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public Session createSession(Session session) {
        List<String> conflicts = detectConflicts(session);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Scheduling conflicts detected: " + String.join("; ", conflicts));
        }
        return sessionRepository.save(session);
    }

    @Override
    public Session updateSession(Long id, Session updated) {
        Session existing = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setTrack(updated.getTrack());
        existing.setRoom(updated.getRoom());
        existing.setSessionDate(updated.getSessionDate());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setType(updated.getType());
        existing.setSpeaker(updated.getSpeaker());
        existing.setMaxCapacity(updated.getMaxCapacity());

        return sessionRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Session> findById(Long id) {
        return sessionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> findAll() {
        return sessionRepository.findByOrderBySessionDateAscStartTimeAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> findBySpeaker(User speaker) {
        return sessionRepository.findBySpeaker(speaker);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> findByDate(LocalDate date) {
        return sessionRepository.findBySessionDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, List<Session>> getScheduleGroupedByDate() {
        List<Session> all = sessionRepository.findByOrderBySessionDateAscStartTimeAsc();
        return all.stream().collect(Collectors.groupingBy(
                s -> s.getSessionDate() != null ? s.getSessionDate() : LocalDate.MIN,
                TreeMap::new,
                Collectors.toList()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> detectConflicts(Session session) {
        List<String> conflicts = new ArrayList<>();

        if (session.getSessionDate() == null || session.getStartTime() == null || session.getEndTime() == null) {
            return conflicts;
        }

        // Room conflict
        if (session.getRoom() != null && !session.getRoom().isBlank()) {
            List<Session> roomConflicts = sessionRepository.findConflictingSessionsByRoom(
                    session.getRoom(), session.getSessionDate(),
                    session.getStartTime(), session.getEndTime());
            roomConflicts.stream()
                    .filter(s -> !s.getId().equals(session.getId()))
                    .forEach(s -> conflicts.add("Room '" + session.getRoom() +
                            "' already booked for: " + s.getTitle() +
                            " (" + s.getStartTime() + "-" + s.getEndTime() + ")"));
        }

        // Speaker conflict
        if (session.getSpeaker() != null) {
            List<Session> speakerConflicts = sessionRepository.findConflictingSessionsBySpeaker(
                    session.getSpeaker(), session.getSessionDate(),
                    session.getStartTime(), session.getEndTime());
            speakerConflicts.stream()
                    .filter(s -> !s.getId().equals(session.getId()))
                    .forEach(s -> conflicts.add("Speaker '" + session.getSpeaker().getFullName() +
                            "' is already scheduled for: " + s.getTitle() +
                            " (" + s.getStartTime() + "-" + s.getEndTime() + ")"));
        }

        return conflicts;
    }

    @Override
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> getAllSessionDates() {
        return sessionRepository.findAllSessionDates();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllTracks() {
        return sessionRepository.findAllTracks();
    }
}
