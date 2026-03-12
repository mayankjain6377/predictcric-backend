package com.sportsprediction.app.repository;

import com.sportsprediction.app.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByPlayerId(String playerId);
    boolean existsByPlayerId(String playerId);
}