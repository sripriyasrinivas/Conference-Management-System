package com.cms.service;

import com.cms.model.Session;
import com.cms.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SessionService {
    Session createSession(Session session);
    Session updateSession(Long id, Session session);
    Optional<Session> findById(Long id);
    List<Session> findAll();
    List<Session> findBySpeaker(User speaker);
    List<Session> findByDate(LocalDate date);
    Map<LocalDate, List<Session>> getScheduleGroupedByDate();
    List<String> detectConflicts(Session session);
    void deleteSession(Long id);
    List<LocalDate> getAllSessionDates();
    List<String> getAllTracks();
}
