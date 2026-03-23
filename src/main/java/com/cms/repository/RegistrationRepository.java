package com.cms.repository;

import com.cms.model.Registration;
import com.cms.model.User;
import com.cms.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByAttendee(User attendee);
    List<Registration> findByConference(Conference conference);
    Optional<Registration> findByAttendeeAndConference(User attendee, Conference conference);
    boolean existsByAttendeeAndConference(User attendee, Conference conference);
    long countByConferenceAndStatus(Conference conference, Registration.RegistrationStatus status);
}
