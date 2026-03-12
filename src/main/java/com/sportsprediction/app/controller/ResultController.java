package com.sportsprediction.app.controller;

import com.sportsprediction.app.dto.response.FaceOffResponse;
import com.sportsprediction.app.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    // Get face-off screen for a challenge
    @GetMapping("/challenge/{challengeId}/faceoff")
    public ResponseEntity<FaceOffResponse> getFaceOff(
            @PathVariable Long challengeId) {
        return ResponseEntity.ok(resultService.getFaceOff(challengeId));
    }
}