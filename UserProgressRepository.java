package com.health.repository;

import com.health.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    List<UserProgress> findByUserIdOrderByDateDesc(Long userId);
    List<UserProgress> findByUserIdOrderByDateAsc(Long userId);
}
