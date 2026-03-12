package com.sportsprediction.app.controller;

import com.sportsprediction.app.repository.ChallengeRepository;
import com.sportsprediction.app.util.ChallengeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ChallengeRepository challengeRepository;

    @GetMapping("/player/{playerId}")
    public ResponseEntity<Map<String, Object>> getNotifications(
            @PathVariable String playerId) {

        // Count pending challenges where I am the opponent
        long pendingChallenges = challengeRepository
                .findByChallengerIdOrOpponentId(playerId, playerId)
                .stream()
                .filter(c -> c.getOpponentId().equals(playerId)
                        && c.getStatus() == ChallengeStatus.PENDING)
                .count();

        // Count completed challenges not yet viewed
        long completedChallenges = challengeRepository
                .findByChallengerIdOrOpponentId(playerId, playerId)
                .stream()
                .filter(c -> c.getStatus() == ChallengeStatus.COMPLETED)
                .count();

        Map<String, Object> result = new HashMap<>();
        result.put("pendingChallenges", pendingChallenges);
        result.put("completedChallenges", completedChallenges);
        result.put("total", pendingChallenges);
        return ResponseEntity.ok(result);
    }
}