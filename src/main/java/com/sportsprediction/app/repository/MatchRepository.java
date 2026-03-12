package com.sportsprediction.app.repository;

import com.sportsprediction.app.model.Match;
import com.sportsprediction.app.util.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByStatus(MatchStatus status);
    boolean existsByCricApiId(String cricApiId);
    Optional<Match> findByCricApiId(String cricApiId);
}