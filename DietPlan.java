package com.health.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "diet_plans")
public class DietPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String planName;

    private String category;   // Fitness, Medical, Women Health, Nutrition
    private String target;     // Fat Loss, Muscle Building, Blood Sugar, etc.
    private String duration;   // 4 Weeks, 6 Weeks, 8 Weeks
    private String status;     // Active, Inactive

    // Maps to setup answers: goal, diet preference, health condition
    private String forGoal;        // Lose Weight, Build Muscle, Stay Fit, Improve Flexibility
    private String forDiet;        // Vegetarian, Non-Vegetarian, Vegan, No Preference
    private String forCondition;   // No, Diabetes, Thyroid, PCOS / Hormonal

    private String description;    // Short description of the plan

    // Weekly schedule stored as JSON string
    // Format: {"Mon":{"breakfast":"...","lunch":"...","dinner":"...","snacks":"..."},...}
    @Column(length = 5000)
    private String weeklySchedule;

    private String created;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getForGoal() { return forGoal; }
    public void setForGoal(String forGoal) { this.forGoal = forGoal; }
    public String getForDiet() { return forDiet; }
    public void setForDiet(String forDiet) { this.forDiet = forDiet; }
    public String getForCondition() { return forCondition; }
    public void setForCondition(String forCondition) { this.forCondition = forCondition; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getWeeklySchedule() { return weeklySchedule; }
    public void setWeeklySchedule(String weeklySchedule) { this.weeklySchedule = weeklySchedule; }
    public String getCreated() { return created; }
    public void setCreated(String created) { this.created = created; }
}
