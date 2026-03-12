package com.sportsprediction.app.repository;

import com.sportsprediction.app.model.Challenge;
import com.sportsprediction.app.util.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // Get all challenges a player is involved in
    List<Challenge> findByChallengerIdOrOpponentId(String challengerId, String opponentId);

    // Get specific challenge between two players for a match
    Optional<Challenge> findByChallengerIdAndOpponentIdAndMatchId(
            String challengerId, String opponentId, Long matchId);

    // Get all completed challenges for scoring
    List<Challenge> findByStatus(ChallengeStatus status);
}