package com.cms.repository;

import com.cms.model.Session;
import com.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findBySpeaker(User speaker);
    List<Session> findBySessionDate(LocalDate date);
    List<Session> findByTrack(String track);
    List<Session> findByOrderBySessionDateAscStartTimeAsc();

    @Query("SELECT s FROM Session s WHERE s.speaker = :speaker AND s.sessionDate = :date")
    List<Session> findBySpeakerAndDate(User speaker, LocalDate date);

    @Query("SELECT s FROM Session s WHERE s.room = :room AND s.sessionDate = :date " +
           "AND ((s.startTime <= :endTime AND s.endTime >= :startTime))")
    List<Session> findConflictingSessionsByRoom(String room, LocalDate date,
                                                 LocalTime startTime, LocalTime endTime);

    @Query("SELECT s FROM Session s WHERE s.speaker = :speaker AND s.sessionDate = :date " +
           "AND ((s.startTime <= :endTime AND s.endTime >= :startTime))")
    List<Session> findConflictingSessionsBySpeaker(User speaker, LocalDate date,
                                                    LocalTime startTime, LocalTime endTime);

    @Query("SELECT DISTINCT s.track FROM Session s WHERE s.track IS NOT NULL")
    List<String> findAllTracks();

    @Query("SELECT DISTINCT s.sessionDate FROM Session s ORDER BY s.sessionDate")
    List<LocalDate> findAllSessionDates();
}
