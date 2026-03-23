package com.cms.service;

import com.cms.model.Registration;
import com.cms.model.User;
import com.cms.model.Conference;
import java.util.List;
import java.util.Optional;

public interface RegistrationService {
    Registration register(User attendee, Conference conference, Registration.RegistrationType type,
                          String dietary, String special);
    Registration updateStatus(Long id, Registration.RegistrationStatus status);
    Optional<Registration> findById(Long id);
    List<Registration> findByAttendee(User attendee);
    List<Registration> findByConference(Conference conference);
    Optional<Registration> findByAttendeeAndConference(User attendee, Conference conference);
    boolean isRegistered(User attendee, Conference conference);
    void cancelRegistration(Long id);
    long countConfirmedByConference(Conference conference);
}
