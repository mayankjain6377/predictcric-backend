package com.sportsprediction.app.controller;

import com.sportsprediction.app.dto.request.PredictionRequest;
import com.sportsprediction.app.dto.response.PredictionResponse;
import com.sportsprediction.app.service.PredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    // Submit a prediction
    @PostMapping("/match/{matchId}")
    public ResponseEntity<PredictionResponse> submit(
            @PathVariable Long matchId,
            @Valid @RequestBody PredictionRequest request) {
        return ResponseEntity.ok(predictionService.submitPrediction(matchId, request));
    }

    // Get all predictions a player made for a match
    @GetMapping("/match/{matchId}/player/{playerId}")
    public ResponseEntity<List<PredictionResponse>> getPlayerPredictions(
            @PathVariable Long matchId,
            @PathVariable String playerId) {
        return ResponseEntity.ok(predictionService.getPlayerPredictions(playerId, matchId));
    }
}

