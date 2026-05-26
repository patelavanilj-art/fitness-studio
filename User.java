package com.health.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password = "*****";
    private String displayPwd;
    private String mobile;
    private String role = "user";
    private String status = "Active";
    private String joinedDate;

    // Profile extras
    private String age;
    private String gender;
    private String height;
    private String weight;
    private String goal;
    private String activityLevel;

    // Setup answers stored as JSON string
    @Column(length = 2000)
    private String setupAnswers;

    // Diet and health condition from setup
    private String dietPreference;
    private String healthCondition;

    // Theme preference
    private String theme = "dark";

    public User() {}

    public User(String name, String email, String password, String mobile, String role) {
        this.name = name;
        this.email = email;
        this.password = "*".repeat(password.length());
        this.displayPwd = password;
        this.mobile = mobile;
        this.role = role;
        this.status = "Active";
        this.joinedDate = LocalDate.now().toString();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDisplayPwd() { return displayPwd; }
    public void setDisplayPwd(String displayPwd) { this.displayPwd = displayPwd; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getJoinedDate() { return joinedDate; }
    public void setJoinedDate(String joinedDate) { this.joinedDate = joinedDate; }
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }
    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    public String getSetupAnswers() { return setupAnswers; }
    public void setSetupAnswers(String setupAnswers) { this.setupAnswers = setupAnswers; }
    public String getDietPreference() { return dietPreference; }
    public void setDietPreference(String dietPreference) { this.dietPreference = dietPreference; }
    public String getHealthCondition() { return healthCondition; }
    public void setHealthCondition(String healthCondition) { this.healthCondition = healthCondition; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}
