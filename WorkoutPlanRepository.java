package com.health.repository;

import com.health.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByGoalContainingIgnoreCaseAndStatus(String goal, String status);
    List<WorkoutPlan> findByLevelContainingIgnoreCaseAndStatus(String level, String status);
    List<WorkoutPlan> findByStatus(String status);
}
