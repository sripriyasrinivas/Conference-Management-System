package com.cms.repository;

import com.cms.model.SessionFeedback;
import com.cms.model.Session;
import com.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback, Long> {
    List<SessionFeedback> findBySession(Session session);
    List<SessionFeedback> findByAttendee(User attendee);
    boolean existsBySessionAndAttendee(Session session, User attendee);

    @Query("SELECT AVG(f.overallRating) FROM SessionFeedback f WHERE f.session = :session")
    Double getAverageRatingForSession(Session session);

    @Query("SELECT AVG(f.speakerRating) FROM SessionFeedback f WHERE f.session.speaker = :speaker")
    Double getAverageSpeakerRating(User speaker);
}
