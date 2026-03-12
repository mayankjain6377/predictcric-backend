package com.sportsprediction.app.controller;

import com.sportsprediction.app.service.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scoring")
@RequiredArgsConstructor
public class ScoringController {

    private final ScoringService scoringService;

    // Trigger scoring for a match (admin sets correct answers first)
    @PostMapping("/match/{matchId}/score")
    public ResponseEntity<String> scoreMatch(@PathVariable Long matchId) {
        scoringService.scoreMatch(matchId);
        return ResponseEntity.ok("Match " + matchId + " scored successfully!");
    }

    // Get a player's score for a match
    @GetMapping("/match/{matchId}/player/{playerId}")
    public ResponseEntity<Integer> getPlayerScore(
            @PathVariable Long matchId,
            @PathVariable String playerId) {
        return ResponseEntity.ok(scoringService.getPlayerScore(matchId,playerId));
    }
}