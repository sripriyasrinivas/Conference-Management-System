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

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PaperService paperService;
    private final ReviewService reviewService;
    private final SessionService sessionService;
    private final ConferenceService conferenceService;
    private final RegistrationService registrationService;
    private final CertificateServiceImpl certificateService;
    private final FeedbackServiceImpl feedbackService;
    private final AbstractServiceImpl abstractService;

    // ── Dashboard ─────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.findAll().size());
        model.addAttribute("totalPapers", paperService.findAll().size());
        model.addAttribute("totalSessions", sessionService.findAll().size());
        model.addAttribute("totalConferences", conferenceService.findAll().size());
        model.addAttribute("submittedPapers", paperService.countByStatus(Paper.Status.SUBMITTED));
        model.addAttribute("underReviewPapers", paperService.countByStatus(Paper.Status.UNDER_REVIEW));
        model.addAttribute("acceptedPapers", paperService.countByStatus(Paper.Status.ACCEPTED));
        model.addAttribute("rejectedPapers", paperService.countByStatus(Paper.Status.REJECTED));
        model.addAttribute("pendingReviews", reviewService.countByStatus(Review.ReviewStatus.ASSIGNED));
        model.addAttribute("recentPapers", paperService.findAll().stream().limit(5).toList());
        model.addAttribute("conferences", conferenceService.findAll());
        return "admin/dashboard";
    }

    // ── User Management ───────────────────────────────────────────────────────
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    // ── Paper Management ──────────────────────────────────────────────────────
    @GetMapping("/papers")
    public String listPapers(Model model) {
        model.addAttribute("papers", paperService.findAll());
        return "admin/papers";
    }

    @GetMapping("/papers/{id}")
    public String viewPaper(@PathVariable Long id, Model model) {
        Paper paper = paperService.findById(id)
                .orElseThrow(() -> new RuntimeException("Paper not found"));
        model.addAttribute("paper", paper);
        model.addAttribute("reviews", reviewService.findByPaper(paper));
        model.addAttribute("reviewers", userService.findByRole(User.Role.REVIEWER));
        model.addAttribute("avgScore", reviewService.getAverageScore(paper));
        return "admin/paper-detail";
    }

    @PostMapping("/papers/{id}/assign-reviewer")
    public String assignReviewer(@PathVariable Long id,
                                  @RequestParam Long reviewerId,
                                  RedirectAttributes ra) {
        Paper paper = paperService.findById(id)
                .orElseThrow(() -> new RuntimeException("Paper not found"));
        User reviewer = userService.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));
        try {
            reviewService.assignReview(paper, reviewer);
            ra.addFlashAttribute("success", "Reviewer assigned successfully.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/papers/" + id;
    }

    @PostMapping("/papers/{id}/status")
    public String updatePaperStatus(@PathVariable Long id,
                                     @RequestParam Paper.Status status,
                                     @RequestParam(required = false) String comments,
                                     RedirectAttributes ra) {
        paperService.updateStatus(id, status, comments);
        ra.addFlashAttribute("success", "Paper status updated to " + status.getDisplayName());
        return "redirect:/admin/papers/" + id;
    }

    // ── Conference Management ─────────────────────────────────────────────────
    @GetMapping("/conferences")
    public String listConferences(Model model) {
        model.addAttribute("conferences", conferenceService.findAll());
        model.addAttribute("newConference", new Conference());
        return "admin/conferences";
    }

    @PostMapping("/conferences/create")
    public String createConference(@ModelAttribute Conference conference,
                                    RedirectAttributes ra) {
        User admin = userService.getCurrentUser();
        conference.setOrganizer(admin);
        conferenceService.create(conference);
        ra.addFlashAttribute("success", "Conference created successfully.");
        return "redirect:/admin/conferences";
    }

    @GetMapping("/conferences/{id}/edit")
    public String editConference(@PathVariable Long id, Model model) {
        Conference conf = conferenceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        model.addAttribute("conference", conf);
        return "admin/conference-edit";
    }

    @PostMapping("/conferences/{id}/edit")
    public String updateConference(@PathVariable Long id,
                                    @ModelAttribute Conference conference,
                                    RedirectAttributes ra) {
        conferenceService.update(id, conference);
        ra.addFlashAttribute("success", "Conference updated.");
        return "redirect:/admin/conferences";
    }

    @PostMapping("/conferences/{id}/status")
    public String updateConferenceStatus(@PathVariable Long id,
                                          @RequestParam Conference.ConferenceStatus status,
                                          RedirectAttributes ra) {
        conferenceService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Conference status updated.");
        return "redirect:/admin/conferences";
    }

    // ── Session Management ────────────────────────────────────────────────────
    @GetMapping("/sessions")
    public String listSessions(Model model) {
        model.addAttribute("sessions", sessionService.findAll());
        model.addAttribute("newSession", new Session());
        model.addAttribute("speakers", userService.findByRole(User.Role.SPEAKER));
        model.addAttribute("acceptedPapers", paperService.findByStatus(Paper.Status.ACCEPTED));
        return "admin/sessions";
    }

    @PostMapping("/sessions/create")
    public String createSession(@ModelAttribute Session session,
                                 @RequestParam(required = false) Long speakerId,
                                 @RequestParam(required = false) Long paperId,
                                 RedirectAttributes ra) {
        if (speakerId != null) {
            userService.findById(speakerId).ifPresent(session::setSpeaker);
        }
        if (paperId != null) {
            paperService.findById(paperId).ifPresent(session::setPaper);
        }
        try {
            sessionService.createSession(session);
            ra.addFlashAttribute("success", "Session created successfully.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sessions";
    }

    @PostMapping("/sessions/{id}/delete")
    public String deleteSession(@PathVariable Long id, RedirectAttributes ra) {
        sessionService.deleteSession(id);
        ra.addFlashAttribute("success", "Session deleted.");
        return "redirect:/admin/sessions";
    }

    // ── Certificates ──────────────────────────────────────────────────────────
    @GetMapping("/certificates")
    public String certificatesPage(Model model) {
        model.addAttribute("conferences", conferenceService.findAll());
        model.addAttribute("certificates", certificateService.findAll());
        return "admin/certificates";
    }

    @PostMapping("/certificates/bulk-issue")
    public String bulkIssueCertificates(@RequestParam Long conferenceId,
                                         RedirectAttributes ra) {
        Conference conf = conferenceService.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        certificateService.bulkIssueParticipationCertificates(conf);
        ra.addFlashAttribute("success", "Participation certificates issued to all confirmed attendees.");
        return "redirect:/admin/certificates";
    }

    @PostMapping("/certificates/issue")
    public String issueCertificate(@RequestParam Long userId,
                                    @RequestParam Long conferenceId,
                                    @RequestParam Certificate.CertificateType type,
                                    RedirectAttributes ra) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Conference conf = conferenceService.findById(conferenceId)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        try {
            certificateService.issueCertificate(user, conf, type);
            ra.addFlashAttribute("success", "Certificate issued successfully.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/certificates";
    }

    // ── Abstract Management ──────────────────────────────────────────────────
    @GetMapping("/abstracts")
    public String listAbstracts(Model model) {
        model.addAttribute("abstracts", abstractService.findAll());
        return "admin/abstracts";
    }

    @PostMapping("/abstracts/{id}/review")
    public String reviewAbstract(@PathVariable Long id,
                                  @RequestParam Abstract.AbstractStatus status,
                                  @RequestParam(required = false) String comments,
                                  RedirectAttributes ra) {
        abstractService.review(id, status, comments);
        ra.addFlashAttribute("success", "Abstract status updated.");
        return "redirect:/admin/abstracts";
    }

    // ── Reports ───────────────────────────────────────────────────────────────
    @GetMapping("/reports")
    public String reportsPage(Model model) {
        model.addAttribute("sessionStats", feedbackService.getAllSessionStats());
        model.addAttribute("papers", paperService.findAll());
        model.addAttribute("conferences", conferenceService.findAll());

        List<Conference> all = conferenceService.findAll();
        if (!all.isEmpty()) {
            Conference latest = all.get(0);
            model.addAttribute("latestConf", latest);
            model.addAttribute("registrations", registrationService.findByConference(latest));
            model.addAttribute("confirmedCount",
                    registrationService.countConfirmedByConference(latest));
        }
        return "admin/reports";
    }
}
