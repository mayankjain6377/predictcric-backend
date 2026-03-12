package com.sportsprediction.app.controller;

import com.sportsprediction.app.service.MatchSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class MatchSyncController {

    private final MatchSyncService matchSyncService;

    // Pull fresh T20 matches from Cricbuzz into DB
    @PostMapping("/matches")
    public ResponseEntity<String> syncMatches() {
        return ResponseEntity.ok(matchSyncService.syncMatches());
    }

    // Fill correct answers + trigger scoring after match ends
    @PostMapping("/matches/{matchId}/results")
    public ResponseEntity<String> fillResults(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchSyncService.fillCorrectAnswers(matchId));
    }
}
