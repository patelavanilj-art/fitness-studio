package com.health.controller;

import com.health.model.*;
import com.health.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private DietPlanRepository dietPlanRepository;
    @Autowired private FeedbackRepository feedbackRepository;
    @Autowired private WorkoutPlanRepository workoutPlanRepository;
    @Autowired private AnnouncementRepository announcementRepository;
    @Autowired private SetupQuestionRepository setupQuestionRepository;

    private String today() { return LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")); }

    // ── Dashboard ──────────────────────────────────────────
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("activeUsers", userRepository.findAll().stream().filter(u -> "Active".equals(u.getStatus())).count());
        model.addAttribute("totalPlans", dietPlanRepository.count());
        model.addAttribute("totalWorkouts", workoutPlanRepository.count());
        model.addAttribute("latestUsers", userRepository.findAll().stream()
            .sorted((a, b) -> b.getId().compareTo(a.getId())).limit(5).toList());
        model.addAttribute("announcements", announcementRepository.findByStatus("Active"));
        return "admin/dashboard";
    }

    @GetMapping("/login")
    public String login() { return "redirect:/login"; }
    @GetMapping("/logout")
    public String logout() { return "admin/logout"; }
    @GetMapping("/settings")
    public String settings() { return "admin/setting"; }

    // ── Users ──────────────────────────────────────────────
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/user";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String name, @RequestParam String email,
                          @RequestParam String password, @RequestParam String mobile,
                          @RequestParam String role, @RequestParam String status) {
        User u = new User(name, email.toLowerCase(), password, mobile, role);
        u.setStatus(status);
        userRepository.save(u);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, @RequestParam String name,
                           @RequestParam String email, @RequestParam String mobile,
                           @RequestParam String role, @RequestParam String status) {
        userRepository.findById(id).ifPresent(u -> {
            u.setName(name); u.setEmail(email.toLowerCase());
            u.setMobile(mobile); u.setRole(role); u.setStatus(status);
            userRepository.save(u);
        });
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // ── Diet Plans ─────────────────────────────────────────
    @GetMapping("/dietplans")
    public String listPlans(Model model) {
        model.addAttribute("plans", dietPlanRepository.findAll());
        return "admin/dietplan";
    }

    @PostMapping("/dietplans/add")
    public String addPlan(@RequestParam String planName, @RequestParam String category,
                          @RequestParam String target, @RequestParam String duration,
                          @RequestParam(required=false, defaultValue="") String forGoal,
                          @RequestParam(required=false, defaultValue="") String forDiet,
                          @RequestParam(required=false, defaultValue="No") String forCondition,
                          @RequestParam(required=false, defaultValue="") String description,
                          @RequestParam(required=false, defaultValue="") String weeklySchedule,
                          @RequestParam String status) {
        DietPlan p = new DietPlan();
        p.setPlanName(planName); p.setCategory(category); p.setTarget(target);
        p.setDuration(duration); p.setForGoal(forGoal); p.setForDiet(forDiet);
        p.setForCondition(forCondition); p.setDescription(description);
        p.setWeeklySchedule(weeklySchedule); p.setStatus(status); p.setCreated(today());
        dietPlanRepository.save(p);
        return "redirect:/admin/dietplans";
    }

    @PostMapping("/dietplans/edit/{id}")
    public String editPlan(@PathVariable Long id,
                           @RequestParam String planName, @RequestParam String category,
                           @RequestParam String target, @RequestParam String duration,
                           @RequestParam(required=false, defaultValue="") String forGoal,
                           @RequestParam(required=false, defaultValue="") String forDiet,
                           @RequestParam(required=false, defaultValue="No") String forCondition,
                           @RequestParam(required=false, defaultValue="") String description,
                           @RequestParam(required=false, defaultValue="") String weeklySchedule,
                           @RequestParam String status) {
        dietPlanRepository.findById(id).ifPresent(p -> {
            p.setPlanName(planName); p.setCategory(category); p.setTarget(target);
            p.setDuration(duration); p.setForGoal(forGoal); p.setForDiet(forDiet);
            p.setForCondition(forCondition); p.setDescription(description);
            p.setWeeklySchedule(weeklySchedule); p.setStatus(status);
            dietPlanRepository.save(p);
        });
        return "redirect:/admin/dietplans";
    }

    @PostMapping("/dietplans/delete/{id}")
    public String deletePlan(@PathVariable Long id) {
        dietPlanRepository.deleteById(id);
        return "redirect:/admin/dietplans";
    }

    // ── Workout Plans ──────────────────────────────────────
    @GetMapping("/workoutplans")
    public String listWorkouts(Model model) {
        model.addAttribute("plans", workoutPlanRepository.findAll());
        return "admin/workoutplan";
    }

    @PostMapping("/workoutplans/add")
    public String addWorkout(@RequestParam String title, @RequestParam String level,
                             @RequestParam String goal, @RequestParam String duration,
                             @RequestParam String daysPerWeek,
                             @RequestParam(required=false, defaultValue="") String description,
                             @RequestParam(required=false) String videoUrl,
                             @RequestParam String status) {
        WorkoutPlan p = new WorkoutPlan();
        p.setTitle(title); p.setLevel(level); p.setGoal(goal);
        p.setDuration(duration); p.setDaysPerWeek(daysPerWeek);
        p.setDescription(description); p.setVideoUrl(videoUrl);
        p.setStatus(status); p.setCreated(today());
        workoutPlanRepository.save(p);
        return "redirect:/admin/workoutplans";
    }

    @PostMapping("/workoutplans/edit/{id}")
    public String editWorkout(@PathVariable Long id, @RequestParam String title,
                              @RequestParam String level, @RequestParam String goal,
                              @RequestParam String duration, @RequestParam String daysPerWeek,
                              @RequestParam(required=false, defaultValue="") String description,
                              @RequestParam(required=false) String videoUrl,
                              @RequestParam String status) {
        workoutPlanRepository.findById(id).ifPresent(p -> {
            p.setTitle(title); p.setLevel(level); p.setGoal(goal);
            p.setDuration(duration); p.setDaysPerWeek(daysPerWeek);
            p.setDescription(description); p.setVideoUrl(videoUrl); p.setStatus(status);
            workoutPlanRepository.save(p);
        });
        return "redirect:/admin/workoutplans";
    }

    @PostMapping("/workoutplans/delete/{id}")
    public String deleteWorkout(@PathVariable Long id) {
        workoutPlanRepository.deleteById(id);
        return "redirect:/admin/workoutplans";
    }

    // ── Announcements ──────────────────────────────────────
    @GetMapping("/announcements")
    public String listAnnouncements(Model model) {
        model.addAttribute("announcements", announcementRepository.findAll());
        return "admin/announcements";
    }

    @PostMapping("/announcements/add")
    public String addAnnouncement(@RequestParam String title, @RequestParam String message,
                                  @RequestParam String type, @RequestParam String status) {
        Announcement a = new Announcement();
        a.setTitle(title); a.setMessage(message); a.setType(type);
        a.setStatus(status); a.setCreatedDate(today());
        announcementRepository.save(a);
        return "redirect:/admin/announcements";
    }

    @PostMapping("/announcements/edit/{id}")
    public String editAnnouncement(@PathVariable Long id, @RequestParam String title,
                                   @RequestParam String message, @RequestParam String type,
                                   @RequestParam String status) {
        announcementRepository.findById(id).ifPresent(a -> {
            a.setTitle(title); a.setMessage(message); a.setType(type); a.setStatus(status);
            announcementRepository.save(a);
        });
        return "redirect:/admin/announcements";
    }

    @PostMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementRepository.deleteById(id);
        return "redirect:/admin/announcements";
    }

    // ── Feedback ───────────────────────────────────────────
    @GetMapping("/feedback")
    public String listFeedback(Model model) {
        model.addAttribute("feedbacks", feedbackRepository.findAll());
        return "admin/feedback";
    }

    @PostMapping("/feedback/markread/{id}")
    public String markRead(@PathVariable Long id) {
        feedbackRepository.findById(id).ifPresent(f -> { f.setStatus("Read"); feedbackRepository.save(f); });
        return "redirect:/admin/feedback";
    }

    @PostMapping("/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id) {
        feedbackRepository.deleteById(id);
        return "redirect:/admin/feedback";
    }

    // ── Reports ────────────────────────────────────────────
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("activeUsers", userRepository.findAll().stream().filter(u -> "Active".equals(u.getStatus())).count());
        model.addAttribute("totalPlans", dietPlanRepository.count());
        return "admin/reports";
    }

    // ── Setup Questions ────────────────────────────────────
    @GetMapping("/setup-questions")
    public String listQuestions(Model model) {
        model.addAttribute("questions", setupQuestionRepository.findAll());
        return "admin/setupquestions";
    }

    @PostMapping("/setup-questions/add")
    public String addQuestion(@RequestParam String question, @RequestParam String options,
                              @RequestParam Integer questionOrder, @RequestParam String status) {
        SetupQuestion q = new SetupQuestion();
        q.setQuestion(question); q.setOptions(options);
        q.setQuestionOrder(questionOrder); q.setStatus(status);
        setupQuestionRepository.save(q);
        return "redirect:/admin/setup-questions";
    }

    @PostMapping("/setup-questions/edit/{id}")
    public String editQuestion(@PathVariable Long id, @RequestParam String question,
                               @RequestParam String options, @RequestParam Integer questionOrder,
                               @RequestParam String status) {
        setupQuestionRepository.findById(id).ifPresent(q -> {
            q.setQuestion(question); q.setOptions(options);
            q.setQuestionOrder(questionOrder); q.setStatus(status);
            setupQuestionRepository.save(q);
        });
        return "redirect:/admin/setup-questions";
    }

    @PostMapping("/setup-questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        setupQuestionRepository.deleteById(id);
        return "redirect:/admin/setup-questions";
    }
}
