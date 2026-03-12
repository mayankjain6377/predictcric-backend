package com.sportsprediction.app.controller;

import com.sportsprediction.app.dto.request.PlayerRequest;
import com.sportsprediction.app.dto.response.PlayerResponse;
import com.sportsprediction.app.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    // Step 1 — frontend calls this when user types their name
    // Returns a suggested ID like "CHIKA2047"
    @GetMapping("/suggest-id")
    public ResponseEntity<String> suggestId(@RequestParam String name) {
        return ResponseEntity.ok(playerService.suggestId(name));
    }

    // Step 2 — player confirms (with suggested or custom ID)
    @PostMapping("/join")
    public ResponseEntity<PlayerResponse> join(
            @Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.ok(playerService.joinAsPlayer(request));
    }

    // Returning player — enters their ID to get back in
    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPlayer(@PathVariable String playerId) {
        try {
            return ResponseEntity.ok(playerService.getPlayer(playerId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}