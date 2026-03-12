package com.sportsprediction.app.repository;

import com.sportsprediction.app.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    // Get all predictions by a player for a match
    List<Prediction> findByPlayerIdAndMatchId(String playerId, Long matchId);

    // Check if player already answered a specific question
    Optional<Prediction> findByPlayerIdAndQuestionId(String playerId, Long questionId);

    // Get all predictions for a match (used in scoring)
    List<Prediction> findByMatchId(Long matchId);
    List<Prediction> findByPlayerId(String playerId);
}