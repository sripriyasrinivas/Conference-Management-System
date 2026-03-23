package com.cms.service.impl;

import com.cms.model.Session;
import com.cms.model.SessionFeedback;
import com.cms.model.User;
import com.cms.repository.SessionFeedbackRepository;
import com.cms.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackServiceImpl {

    private final SessionFeedbackRepository feedbackRepository;
    private final SessionRepository sessionRepository;

    public SessionFeedback submitFeedback(User attendee, Long sessionId,
                                           int contentRating, int speakerRating,
                                           int overallRating, String comments) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (feedbackRepository.existsBySessionAndAttendee(session, attendee)) {
            throw new RuntimeException("Feedback already submitted for this session.");
        }

        SessionFeedback fb = SessionFeedback.builder()
                .session(session)
                .attendee(attendee)
                .contentRating(contentRating)
                .speakerRating(speakerRating)
                .overallRating(overallRating)
                .comments(comments)
                .build();

        return feedbackRepository.save(fb);
    }

    @Transactional(readOnly = true)
    public List<SessionFeedback> getBySession(Session session) {
        return feedbackRepository.findBySession(session);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSessionStats(Session session) {
        List<SessionFeedback> feedbacks = feedbackRepository.findBySession(session);
        Map<String, Object> stats = new LinkedHashMap<>();
        if (feedbacks.isEmpty()) {
            stats.put("count", 0);
            stats.put("avgContent", 0.0);
            stats.put("avgSpeaker", 0.0);
            stats.put("avgOverall", 0.0);
            return stats;
        }
        stats.put("count", feedbacks.size());
        stats.put("avgContent", feedbacks.stream().mapToInt(SessionFeedback::getContentRating).average().orElse(0));
        stats.put("avgSpeaker", feedbacks.stream().mapToInt(SessionFeedback::getSpeakerRating).average().orElse(0));
        stats.put("avgOverall", feedbacks.stream().mapToInt(SessionFeedback::getOverallRating).average().orElse(0));
        return stats;
    }

    @Transactional(readOnly = true)
    public Map<Session, Map<String, Object>> getAllSessionStats() {
        List<Session> sessions = sessionRepository.findAll();
        Map<Session, Map<String, Object>> result = new LinkedHashMap<>();
        for (Session s : sessions) {
            result.put(s, getSessionStats(s));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public boolean hasSubmittedFeedback(User attendee, Long sessionId) {
        return sessionRepository.findById(sessionId)
                .map(s -> feedbackRepository.existsBySessionAndAttendee(s, attendee))
                .orElse(false);
    }
}
