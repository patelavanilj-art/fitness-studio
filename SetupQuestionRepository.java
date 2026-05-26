package com.health.repository;

import com.health.model.SetupQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SetupQuestionRepository extends JpaRepository<SetupQuestion, Long> {
    List<SetupQuestion> findByStatusOrderByQuestionOrderAsc(String status);
}
