package com.sportsprediction.app.controller;

import com.sportsprediction.app.dto.response.MatchResponse;
import com.sportsprediction.app.dto.response.QuestionResponse;
import com.sportsprediction.app.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // Get all upcoming matches
    @GetMapping("/upcoming")
    public ResponseEntity<List<MatchResponse>> getUpcoming() {
        return ResponseEntity.ok(matchService.getUpcomingMatches());
    }

    // Get single match
    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponse> getMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.getMatchById(matchId));
    }

    // Get questions for a match
    @GetMapping("/{matchId}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.getQuestionsByMatchId(matchId));
    }
}