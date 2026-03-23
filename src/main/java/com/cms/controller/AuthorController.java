package com.cms.controller;

import com.cms.model.*;
import com.cms.service.*;
import com.cms.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/author")
@PreAuthorize("hasRole('AUTHOR')")
@RequiredArgsConstructor
public class AuthorController {

    private final UserService userService;
    private final PaperService paperService;
    private final ReviewService reviewService;
    private final AbstractServiceImpl abstractService;
    private final ConferenceService conferenceService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User author = userService.getCurrentUser();
        model.addAttribute("author", author);
        model.addAttribute("papers", paperService.findByAuthor(author));
        model.addAttribute("abstracts", abstractService.findByAuthor(author));
        return "author/dashboard";
    }

    // ── Paper Submission ──────────────────────────────────────────────────────
    @GetMapping("/papers/submit")
    public String submitForm(Model model) {
        model.addAttribute("paper", new Paper());
        return "author/submit-paper";
    }

    @PostMapping("/papers/submit")
    public String submitPaper(@ModelAttribute Paper paper,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               RedirectAttributes ra) {
        User author = userService.getCurrentUser();
        try {
            paperService.submitPaper(paper, file, author);
            ra.addFlashAttribute("success", "Paper submitted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Submission failed: " + e.getMessage());
        }
        return "redirect:/author/dashboard";
    }

    // ── View My Papers ────────────────────────────────────────────────────────
    @GetMapping("/papers")
    public String myPapers(Model model) {
        User author = userService.getCurrentUser();
        model.addAttribute("papers", paperService.findByAuthor(author));
        return "author/my-papers";
    }

    @GetMapping("/papers/{id}")
    public String viewPaper(@PathVariable Long id, Model model) {
        User author = userService.getCurrentUser();
        Paper paper = paperService.findById(id)
                .orElseThrow(() -> new RuntimeException("Paper not found"));
        if (!paper.getAuthor().getId().equals(author.getId())) {
            return "redirect:/author/papers";
        }
        model.addAttribute("paper", paper);
        model.addAttribute("reviews", reviewService.findByPaper(paper));
        model.addAttribute("avgScore", reviewService.getAverageScore(paper));
        return "author/paper-detail";
    }

    // ── Revise Paper ──────────────────────────────────────────────────────────
    @GetMapping("/papers/{id}/revise")
    public String reviseForm(@PathVariable Long id, Model model) {
        Paper paper = paperService.findById(id)
                .orElseThrow(() -> new RuntimeException("Paper not found"));
        model.addAttribute("paper", paper);
        return "author/revise-paper";
    }

    @PostMapping("/papers/{id}/revise")
    public String revisePaper(@PathVariable Long id,
                               @ModelAttribute Paper updated,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               RedirectAttributes ra) {
        try {
            paperService.resubmitPaper(id, updated, file);
            ra.addFlashAttribute("success", "Paper revised and resubmitted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Revision failed: " + e.getMessage());
        }
        return "redirect:/author/papers/" + id;
    }

    // ── Abstract Submission ───────────────────────────────────────────────────
    @GetMapping("/abstracts/submit")
    public String abstractForm(Model model) {
        model.addAttribute("abstract", new Abstract());
        return "author/submit-abstract";
    }

    @PostMapping("/abstracts/submit")
    public String submitAbstract(@ModelAttribute Abstract abs, RedirectAttributes ra) {
        User author = userService.getCurrentUser();
        abstractService.submit(abs, author);
        ra.addFlashAttribute("success", "Abstract submitted successfully.");
        return "redirect:/author/dashboard";
    }

    @GetMapping("/abstracts")
    public String myAbstracts(Model model) {
        User author = userService.getCurrentUser();
        model.addAttribute("abstracts", abstractService.findByAuthor(author));
        return "author/my-abstracts";
    }

    @GetMapping("/abstracts/{id}/revise")
    public String reviseAbstractForm(@PathVariable Long id, Model model) {
        Abstract abs = abstractService.findById(id)
                .orElseThrow(() -> new RuntimeException("Abstract not found"));
        model.addAttribute("abstract", abs);
        return "author/revise-abstract";
    }

    @PostMapping("/abstracts/{id}/revise")
    public String reviseAbstract(@PathVariable Long id,
                                  @ModelAttribute Abstract updated,
                                  RedirectAttributes ra) {
        abstractService.revise(id, updated);
        ra.addFlashAttribute("success", "Abstract revised and resubmitted.");
        return "redirect:/author/abstracts";
    }

    // ── Profile ───────────────────────────────────────────────────────────────
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "author/profile";
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
        return "redirect:/author/profile";
    }
}
