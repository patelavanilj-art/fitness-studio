package com.health.controller;

import com.health.repository.DietPlanRepository;
import com.health.repository.WorkoutPlanRepository;
import com.health.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FitnessController {

    @Autowired private DietPlanRepository dietPlanRepository;
    @Autowired private WorkoutPlanRepository workoutPlanRepository;
    @Autowired private AnnouncementRepository announcementRepository;

    @GetMapping("/")
    public String index() { return "landing"; }

    @GetMapping("/home")
    public String home() { return "landing"; }

    @GetMapping("/login")
    public String login() { return "user/userlogin"; }

    @GetMapping("/user/login")
    public String userLogin() { return "user/userlogin"; }

    @GetMapping("/user/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("announcements", announcementRepository.findByStatus("Active"));
        return "user/dashboard";
    }

    @GetMapping("/user/setup")
    public String setup() { return "user/setup"; }

    @GetMapping("/setup")
    public String setupRedirect() { return "redirect:/user/setup"; }

    @GetMapping("/user/workout")
    public String workout(Model model) {
        model.addAttribute("workoutPlans", workoutPlanRepository.findByStatus("Active"));
        return "user/workout";
    }

    @GetMapping("/user/profile")
    public String profile() { return "user/profile"; }

    @GetMapping("/user/progress")
    public String progress() { return "user/progress"; }

    @GetMapping("/user/plane")
    public String plane(Model model) {
        model.addAttribute("plans", dietPlanRepository.findByStatus("Active"));
        return "user/plane";
    }

    @GetMapping("/user/feedback")
    public String feedback() { return "user/feedback"; }

    @GetMapping("/user/goal")
    public String goal() { return "user/goal"; }

    @GetMapping("/user/settings")
    public String settings() { return "user/settings"; }
}
