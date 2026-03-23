package com.cms.controller;

import com.cms.model.*;
import com.cms.service.*;
import com.cms.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/attendee")
@PreAuthorize("hasRole('ATTENDEE')")
@RequiredArgsConstructor
public class AttendeeController {

    private final UserService userService;
    private final ConferenceService conferenceService;
    private final RegistrationService registrationService;
    private final SessionService sessionService;
    private final FeedbackServiceImpl feedbackService;
    private final CertificateServiceImpl certificateService;
    private final MessageServiceImpl messageService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User attendee = userService.getCurrentUser();
        model.addAttribute("attendee", attendee);
        model.addAttribute("myRegistrations", registrationService.findByAttendee(attendee));
        model.addAttribute("conferences", conferenceService.findAll());
        model.addAttribute("myCertificates", certificateService.findByRecipient(attendee));
        model.addAttribute("unreadMessages", messageService.countUnread(attendee));
        return "attendee/dashboard";
    }

    // ── Conference Registration ───────────────────────────────────────────────
    @GetMapping("/conferences")
    public String browsConferences(Model model) {
        User attendee = userService.getCurrentUser();
        model.addAttribute("conferences", conferenceService.findAll());
        model.addAttribute("myRegistrations", registrationService.findByAttendee(attendee));
        return "attendee/conferences";
    }

    @GetMapping("/conferences/{id}/register")
    public String registerForm(@PathVariable Long id, Model model) {
        Conference conf = conferenceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        User attendee = userService.getCurrentUser();
        if (registrationService.isRegistered(attendee, conf)) {
            return "redirect:/attendee/conferences";
        }
        model.addAttribute("conference", conf);
        model.addAttribute("types", Registration.RegistrationType.values());
        return "attendee/register-conference";
    }

    @PostMapping("/conferences/{id}/register")
    public String doRegister(@PathVariable Long id,
                              @RequestParam Registration.RegistrationType type,
                              @RequestParam(required = false) String dietary,
                              @RequestParam(required = false) String special,
                              RedirectAttributes ra) {
        User attendee = userService.getCurrentUser();
        Conference conf = conferenceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        try {
            Registration reg = registrationService.register(attendee, conf, type, dietary, special);
            if (reg.getStatus() == Registration.RegistrationStatus.WAITLISTED) {
                ra.addFlashAttribute("warning", "Added to waitlist. Conference is at capacity.");
            } else {
                ra.addFlashAttribute("success", "Successfully registered for " + conf.getName() + "!");
            }
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/attendee/conferences";
    }

    @PostMapping("/registrations/{id}/cancel")
    public String cancelRegistration(@PathVariable Long id, RedirectAttributes ra) {
        registrationService.cancelRegistration(id);
        ra.addFlashAttribute("success", "Registration cancelled.");
        return "redirect:/attendee/dashboard";
    }

    // ── Schedule ──────────────────────────────────────────────────────────────
    @GetMapping("/schedule")
    public String viewSchedule(Model model) {
        model.addAttribute("schedule", sessionService.getScheduleGroupedByDate());
        model.addAttribute("tracks", sessionService.getAllTracks());
        return "attendee/schedule";
    }

    // ── Feedback ──────────────────────────────────────────────────────────────
    @GetMapping("/feedback")
    public String feedbackPage(Model model) {
        User attendee = userService.getCurrentUser();
        model.addAttribute("sessions", sessionService.findAll());
        model.addAttribute("attendee", attendee);
        return "attendee/feedback";
    }

    @PostMapping("/feedback/{sessionId}")
    public String submitFeedback(@PathVariable Long sessionId,
                                  @RequestParam int contentRating,
                                  @RequestParam int speakerRating,
                                  @RequestParam int overallRating,
                                  @RequestParam(required = false) String comments,
                                  RedirectAttributes ra) {
        User attendee = userService.getCurrentUser();
        try {
            feedbackService.submitFeedback(attendee, sessionId, contentRating,
                    speakerRating, overallRating, comments);
            ra.addFlashAttribute("success", "Thank you for your feedback!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/attendee/feedback";
    }

    // ── Certificates ──────────────────────────────────────────────────────────
    @GetMapping("/certificates")
    public String myCertificates(Model model) {
        User attendee = userService.getCurrentUser();
        model.addAttribute("certificates", certificateService.findByRecipient(attendee));
        return "attendee/certificates";
    }

    // ── Profile ───────────────────────────────────────────────────────────────
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "attendee/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User updated, RedirectAttributes ra) {
        User current = userService.getCurrentUser();
        current.setFullName(updated.getFullName());
        current.setInstitution(updated.getInstitution());
        current.setBio(updated.getBio());
        current.setPhone(updated.getPhone());
        userService.updateUser(current);
        ra.addFlashAttribute("success", "Profile updated.");
        return "redirect:/attendee/profile";
    }
}
