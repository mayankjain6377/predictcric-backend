package com.sportsprediction.app.repository;

import com.sportsprediction.app.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByMatchId(Long matchId);
}