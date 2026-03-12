package com.sportsprediction.app.controller;
// Add these imports at top
import com.sportsprediction.app.model.Question;
import com.sportsprediction.app.dto.request.QuestionRequest;
import com.sportsprediction.app.service.*;
import com.sportsprediction.app.dto.response.MatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @Value("${admin.secret}")
    private String adminSecret;

    private final MatchSyncService matchSyncService;
    private final ScoringService scoringService;
    private final MatchService matchService;
    private final PlayerService playerService;
    private final ChallengeService challengeService;
    private final PredictionService predictionService;



    // Validate admin password
    private boolean isValid(String secret) {
        return adminSecret.equals(secret);
    }

    @PostMapping("/matches/{matchId}/create-default-questions")
    public ResponseEntity<?> createDefaultQuestions(
            @PathVariable Long matchId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        return ResponseEntity.ok(matchSyncService.createDefaultQuestionsForMatch(matchId));
    }

    // GET all matches (for admin to see)
    @GetMapping("/matches")
    public ResponseEntity<?> getAllMatches(@RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Invalid admin password");
        List<MatchResponse> matches = matchService.getAllMatches();
        return ResponseEntity.ok(matches);
    }

    // Sync matches from Cricbuzz
    @PostMapping("/sync-matches")
    public ResponseEntity<?> syncMatches(@RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Invalid admin password");
        return ResponseEntity.ok(matchSyncService.syncMatches());
    }

    // Fill correct answers + trigger scoring for a match
    @PostMapping("/score-match/{matchId}")
    public ResponseEntity<?> scoreMatch(
            @PathVariable Long matchId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Invalid admin password");
        scoringService.scoreMatch(matchId);
        return ResponseEntity.ok("Match " + matchId + " scored successfully!");
    }

    // Fill answers from API then score
    @PostMapping("/fill-results/{matchId}")
    public ResponseEntity<?> fillResults(
            @PathVariable Long matchId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Invalid admin password");
        return ResponseEntity.ok(matchSyncService.fillCorrectAnswers(matchId));
    }

    // Manually set correct answer for a question
    @PostMapping("/set-answer")
    public ResponseEntity<?> setAnswer(
            @RequestBody Map<String, String> body,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Invalid admin password");
        Long questionId = Long.valueOf(body.get("questionId"));
        String answer = body.get("correctAnswer");
        matchSyncService.setCorrectAnswer(questionId, answer);
        return ResponseEntity.ok("Answer set for question " + questionId);
    }

    // ── Add new question to a match ──────────────────────────
    @PostMapping("/matches/{matchId}/questions")
    public ResponseEntity<?> addQuestion(
            @PathVariable Long matchId,
            @RequestBody QuestionRequest request,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Invalid admin password");
        return ResponseEntity.ok(matchSyncService.addQuestion(matchId, request));
    }

    // ── Delete a question ────────────────────────────────────
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable Long questionId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Invalid admin password");
        matchSyncService.deleteQuestion(questionId);
        return ResponseEntity.ok("Question deleted successfully");
    }

    // ── Update existing question ─────────────────────────────
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionRequest request,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Invalid admin password");
        return ResponseEntity.ok(matchSyncService.updateQuestion(questionId, request));
    }
    // ── PLAYERS ──────────────────────────────────────────────

    @GetMapping("/players")
    public ResponseEntity<?> getAllPlayers(
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @DeleteMapping("/players/{playerId}")
    public ResponseEntity<?> deletePlayer(
            @PathVariable String playerId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        playerService.deletePlayer(playerId);
        return ResponseEntity.ok("Player deleted");
    }

// ── PREDICTIONS ───────────────────────────────────────────

    @GetMapping("/predictions/match/{matchId}")
    public ResponseEntity<?> getPredictionsByMatch(
            @PathVariable Long matchId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        return ResponseEntity.ok(predictionService.getPredictionsByMatch(matchId));
    }

    @GetMapping("/predictions/player/{playerId}")
    public ResponseEntity<?> getPredictionsByPlayer(
            @PathVariable String playerId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        return ResponseEntity.ok(predictionService.getPredictionsByPlayer(playerId));
    }

    @DeleteMapping("/predictions/{predictionId}")
    public ResponseEntity<?> deletePrediction(
            @PathVariable Long predictionId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        predictionService.deletePrediction(predictionId);
        return ResponseEntity.ok("Prediction deleted");
    }

// ── CHALLENGES ────────────────────────────────────────────

    @GetMapping("/challenges")
    public ResponseEntity<?> getAllChallenges(
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    @DeleteMapping("/challenges/{challengeId}")
    public ResponseEntity<?> deleteChallenge(
            @PathVariable Long challengeId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.ok("Challenge deleted");
    }

    @PutMapping("/challenges/{challengeId}/reset")
    public ResponseEntity<?> resetChallenge(
            @PathVariable Long challengeId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        challengeService.resetChallenge(challengeId);
        return ResponseEntity.ok("Challenge reset to ACCEPTED");
    }

    @PutMapping("/challenges/{challengeId}/set-winner")
    public ResponseEntity<?> setWinner(
            @PathVariable Long challengeId,
            @RequestParam String winnerId,
            @RequestHeader("X-Admin-Secret") String secret) {
        if (!isValid(secret)) return ResponseEntity.status(403).body("Unauthorized");
        challengeService.setWinner(challengeId, winnerId);
        return ResponseEntity.ok("Winner set to " + winnerId);
    }
}