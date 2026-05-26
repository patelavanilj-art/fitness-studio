package com.health.controller;

import com.health.model.DietPlan;
import com.health.repository.AnnouncementRepository;
import com.health.repository.SetupQuestionRepository;
import com.health.repository.WorkoutPlanRepository;
import com.health.repository.DietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserDataController {

    @Autowired private WorkoutPlanRepository workoutPlanRepository;
    @Autowired private AnnouncementRepository announcementRepository;
    @Autowired private SetupQuestionRepository setupQuestionRepository;
    @Autowired private DietPlanRepository dietPlanRepository;

    @GetMapping("/workoutplans")
    public ResponseEntity<?> getWorkoutPlans(@RequestParam(required = false) String goal,
                                              @RequestParam(required = false) String level) {
        java.util.List<com.health.model.WorkoutPlan> plans;
        if (goal != null && !goal.isEmpty()) {
            plans = workoutPlanRepository.findByGoalContainingIgnoreCaseAndStatus(goal, "Active");
        } else if (level != null && !level.isEmpty()) {
            plans = workoutPlanRepository.findByLevelContainingIgnoreCaseAndStatus(level, "Active");
        } else {
            plans = workoutPlanRepository.findByStatus("Active");
        }
        // If level also provided alongside goal, filter further
        if (goal != null && !goal.isEmpty() && level != null && !level.isEmpty()) {
            final String lvl = level.trim();
            java.util.List<com.health.model.WorkoutPlan> filtered = plans.stream()
                .filter(p -> p.getLevel() != null && p.getLevel().equalsIgnoreCase(lvl))
                .collect(java.util.stream.Collectors.toList());
            // Fallback: if no exact level match, return all goal-matched plans
            if (!filtered.isEmpty()) plans = filtered;
        }
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/dietplans")
    public ResponseEntity<?> getDietPlans(
            @RequestParam(required = false) String goal,
            @RequestParam(required = false) String diet,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String category) {

        final String g = (goal != null) ? goal.trim() : "";
        final String d = (diet != null) ? diet.trim() : "";
        final String c = (condition != null) ? condition.trim() : "No";

        // Step 1: filter by goal
        List<DietPlan> byGoal = dietPlanRepository.findByStatus("Active");
        if (!g.isEmpty()) {
            byGoal = byGoal.stream()
                .filter(p -> p.getForGoal() != null && p.getForGoal().equalsIgnoreCase(g))
                .collect(Collectors.toList());
        }

        // Step 2: filter by diet (strict — only exact match, skip if "No Preference")
        List<DietPlan> byDiet = byGoal;
        if (!d.isEmpty() && !d.equalsIgnoreCase("No Preference")) {
            byDiet = byGoal.stream()
                .filter(p -> p.getForDiet() != null && p.getForDiet().equalsIgnoreCase(d))
                .collect(Collectors.toList());
        }

        // Step 3: filter by condition (strict exact match)
        List<DietPlan> result;
        if (!c.isEmpty() && !c.equalsIgnoreCase("No")) {
            // Try exact condition match first
            result = byDiet.stream()
                .filter(p -> p.getForCondition() != null && p.getForCondition().equalsIgnoreCase(c))
                .collect(Collectors.toList());
            // Fallback: if no condition-specific plan found, show "No" condition plans with same goal+diet
            if (result.isEmpty()) {
                result = byDiet.stream()
                    .filter(p -> "No".equalsIgnoreCase(p.getForCondition()))
                    .collect(Collectors.toList());
            }
        } else {
            // condition = "No" — show only plans with forCondition = "No"
            result = byDiet.stream()
                .filter(p -> "No".equalsIgnoreCase(p.getForCondition()))
                .collect(Collectors.toList());
        }

        // Optional category filter
        if (category != null && !category.isEmpty()) {
            final String cat = category.trim();
            result = result.stream()
                .filter(p -> cat.equalsIgnoreCase(p.getCategory()))
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(result.isEmpty() ? result : result.subList(0, 1));
    }

    @GetMapping("/dietplans/debug")
    public ResponseEntity<?> debugDietPlans() {
        List<DietPlan> all = dietPlanRepository.findByStatus("Active");
        return ResponseEntity.ok(all.stream().map(p -> {
            java.util.Map<String, String> m = new java.util.LinkedHashMap<>();
            m.put("id", String.valueOf(p.getId()));
            m.put("planName", p.getPlanName());
            m.put("forGoal", p.getForGoal());
            m.put("forDiet", p.getForDiet());
            m.put("forCondition", p.getForCondition());
            m.put("hasSchedule", p.getWeeklySchedule() != null && !p.getWeeklySchedule().isEmpty() ? "YES" : "NO");
            return m;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/announcements")
    public ResponseEntity<?> getAnnouncements() {
        return ResponseEntity.ok(announcementRepository.findByStatus("Active"));
    }

    @GetMapping("/setup-questions")
    public ResponseEntity<?> getSetupQuestions() {
        return ResponseEntity.ok(setupQuestionRepository.findByStatusOrderByQuestionOrderAsc("Active"));
    }
}
