package com.cms.service.impl;

import com.cms.model.*;
import com.cms.repository.RegistrationRepository;
import com.cms.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;

    @Override
    public Registration register(User attendee, Conference conference,
                                  Registration.RegistrationType type,
                                  String dietary, String special) {
        if (registrationRepository.existsByAttendeeAndConference(attendee, conference)) {
            throw new RuntimeException("Already registered for this conference.");
        }

        long confirmed = registrationRepository.countByConferenceAndStatus(
                conference, Registration.RegistrationStatus.CONFIRMED);
        Registration.RegistrationStatus status = (conference.getMaxAttendees() > 0
                && confirmed >= conference.getMaxAttendees())
                ? Registration.RegistrationStatus.WAITLISTED
                : Registration.RegistrationStatus.CONFIRMED;

        Registration reg = Registration.builder()
                .attendee(attendee)
                .conference(conference)
                .type(type)
                .status(status)
                .dietaryPreferences(dietary)
                .specialRequirements(special)
                .confirmedAt(status == Registration.RegistrationStatus.CONFIRMED ? LocalDateTime.now() : null)
                .build();

        return registrationRepository.save(reg);
    }

    @Override
    public Registration updateStatus(Long id, Registration.RegistrationStatus status) {
        Registration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found: " + id));
        reg.setStatus(status);
        if (status == Registration.RegistrationStatus.CONFIRMED) {
            reg.setConfirmedAt(LocalDateTime.now());
        }
        return registrationRepository.save(reg);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Registration> findById(Long id) {
        return registrationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Registration> findByAttendee(User attendee) {
        return registrationRepository.findByAttendee(attendee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Registration> findByConference(Conference conference) {
        return registrationRepository.findByConference(conference);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Registration> findByAttendeeAndConference(User attendee, Conference conference) {
        return registrationRepository.findByAttendeeAndConference(attendee, conference);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRegistered(User attendee, Conference conference) {
        return registrationRepository.existsByAttendeeAndConference(attendee, conference);
    }

    @Override
    public void cancelRegistration(Long id) {
        Registration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found: " + id));
        reg.setStatus(Registration.RegistrationStatus.CANCELLED);
        registrationRepository.save(reg);
    }

    @Override
    @Transactional(readOnly = true)
    public long countConfirmedByConference(Conference conference) {
        return registrationRepository.countByConferenceAndStatus(conference,
                Registration.RegistrationStatus.CONFIRMED);
    }
}
