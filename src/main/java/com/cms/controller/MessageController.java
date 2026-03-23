package com.cms.controller;

import com.cms.model.User;
import com.cms.service.UserService;
import com.cms.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageServiceImpl messageService;
    private final UserService userService;

    @GetMapping("/inbox")
    public String inbox(Model model) {
        User current = userService.getCurrentUser();
        model.addAttribute("messages", messageService.getInbox(current));
        model.addAttribute("sentMessages", messageService.getSent(current));
        model.addAttribute("users", userService.findAll().stream()
                .filter(u -> !u.getId().equals(current.getId())).toList());
        return "common/messages";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam Long receiverId,
                               @RequestParam String subject,
                               @RequestParam String content,
                               RedirectAttributes ra) {
        User sender = userService.getCurrentUser();
        try {
            messageService.sendMessage(sender, receiverId, subject, content);
            ra.addFlashAttribute("success", "Message sent successfully.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/messages/inbox";
    }
}
