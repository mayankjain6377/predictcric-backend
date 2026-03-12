package com.sportsprediction.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PredictionResponse {
    private Long id;
    private String playerId;
    private Long questionId;
    private String questionText;
    private String selectedAnswer;
    private Boolean isCorrect;
}