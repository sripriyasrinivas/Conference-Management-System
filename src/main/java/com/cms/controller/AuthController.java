package com.cms.controller;

import com.cms.model.User;
import com.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                             @RequestParam(required = false) String logout,
                             Model model) {
        if (error != null) model.addAttribute("error", "Invalid username or password.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", new User.Role[]{
            User.Role.AUTHOR, User.Role.REVIEWER, User.Role.SPEAKER, User.Role.ATTENDEE
        });
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                                @RequestParam String confirmPassword,
                                RedirectAttributes ra) {
        if (!user.getPassword().equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/register";
        }
        if (userService.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Username already taken.");
            return "redirect:/register";
        }
        if (userService.existsByEmail(user.getEmail())) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register";
        }
        userService.registerUser(user);
        ra.addFlashAttribute("success", "Registration successful! Please log in.");
        return "redirect:/login";
    }
}
