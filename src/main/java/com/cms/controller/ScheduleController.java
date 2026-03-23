package com.cms.controller;

import com.cms.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final SessionService sessionService;

    @GetMapping
    public String viewSchedule(Model model) {
        model.addAttribute("schedule", sessionService.getScheduleGroupedByDate());
        model.addAttribute("tracks", sessionService.getAllTracks());
        model.addAttribute("allSessions", sessionService.findAll());
        return "common/schedule";
    }
}
