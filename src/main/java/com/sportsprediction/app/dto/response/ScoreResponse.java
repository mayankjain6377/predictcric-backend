package com.sportsprediction.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoreResponse {
    private String playerId;
    private String playerName;
    private int correctAnswers;
    private int totalQuestions;
    private int coinsEarned;
}