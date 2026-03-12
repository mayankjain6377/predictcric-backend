package com.sportsprediction.app.controller;

import com.sportsprediction.app.dto.request.ChallengeRequest;
import com.sportsprediction.app.dto.response.ChallengeResponse;
import com.sportsprediction.app.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    // Create a new challenge
    @PostMapping
    public ResponseEntity<ChallengeResponse> create(
            @Valid @RequestBody ChallengeRequest request) {
        return ResponseEntity.ok(challengeService.createChallenge(request));
    }

    // Accept a challenge
    @PutMapping("/{challengeId}/accept")
    public ResponseEntity<ChallengeResponse> accept(
            @PathVariable Long challengeId,
            @RequestParam String opponentId) {
        return ResponseEntity.ok(challengeService.acceptChallenge(challengeId, opponentId));
    }

    // Get all challenges for a player
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<ChallengeResponse>> getMyChallenges(
            @PathVariable String playerId) {
        return ResponseEntity.ok(challengeService.getMyChallenges(playerId));
    }

    // Get single challenge
    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeResponse> getChallenge(
            @PathVariable Long challengeId) {
        return ResponseEntity.ok(challengeService.getChallengeById(challengeId));
    }
}