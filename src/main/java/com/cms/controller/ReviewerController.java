package com.cms.controller;

import com.cms.model.*;
import com.cms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviewer")
@PreAuthorize("hasRole('REVIEWER')")
@RequiredArgsConstructor
public class ReviewerController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final PaperService paperService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User reviewer = userService.getCurrentUser();
        model.addAttribute("reviewer", reviewer);
        model.addAttribute("allReviews", reviewService.findByReviewer(reviewer));
        model.addAttribute("pendingReviews", reviewService.findPendingByReviewer(reviewer));
        model.addAttribute("completedCount",
                reviewService.findByReviewer(reviewer).stream()
                        .filter(r -> r.getStatus() == Review.ReviewStatus.SUBMITTED).count());
        return "reviewer/dashboard";
    }

    @GetMapping("/reviews")
    public String myReviews(Model model) {
        User reviewer = userService.getCurrentUser();
        model.addAttribute("reviews", reviewService.findByReviewer(reviewer));
        return "reviewer/my-reviews";
    }

    @GetMapping("/reviews/{id}")
    public String viewReview(@PathVariable Long id, Model model) {
        Review review = reviewService.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        User reviewer = userService.getCurrentUser();
        if (!review.getReviewer().getId().equals(reviewer.getId())) {
            return "redirect:/reviewer/reviews";
        }
        model.addAttribute("review", review);
        model.addAttribute("paper", review.getPaper());
        model.addAttribute("recommendations", Review.Recommendation.values());
        return "reviewer/review-form";
    }

    @PostMapping("/reviews/{id}/submit")
    public String submitReview(@PathVariable Long id,
                                @ModelAttribute Review reviewData,
                                RedirectAttributes ra) {
        try {
            reviewService.submitReview(id, reviewData);
            ra.addFlashAttribute("success", "Review submitted successfully.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "Failed to submit: " + e.getMessage());
        }
        return "redirect:/reviewer/reviews";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "reviewer/profile";
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
        return "redirect:/reviewer/profile";
    }
}
