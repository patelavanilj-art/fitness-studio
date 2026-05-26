package com.health.repository;

import com.health.model.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    List<DietPlan> findByPlanNameContainingIgnoreCase(String planName);
    List<DietPlan> findByStatus(String status);
    List<DietPlan> findByCategoryAndStatus(String category, String status);
    List<DietPlan> findByForGoalIgnoreCaseAndStatusAndForConditionIgnoreCase(String forGoal, String status, String forCondition);
    List<DietPlan> findByForGoalIgnoreCaseAndStatusAndForDietIgnoreCaseAndForConditionIgnoreCase(String forGoal, String status, String forDiet, String forCondition);
}
