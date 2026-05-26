package com.health.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String date;
    private Double weight;
    private Integer workoutMinutes;
    private Integer waterGlasses;
    private Integer caloriesBurned;
    private String notes;

    public UserProgress() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Integer getWorkoutMinutes() { return workoutMinutes; }
    public void setWorkoutMinutes(Integer workoutMinutes) { this.workoutMinutes = workoutMinutes; }
    public Integer getWaterGlasses() { return waterGlasses; }
    public void setWaterGlasses(Integer waterGlasses) { this.waterGlasses = waterGlasses; }
    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
