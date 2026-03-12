package com.sportsprediction.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PredictionRequest {

    @NotBlank(message = "Player ID is required")
    private String playerId;          // #CHIKA2847

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Selected answer is required")
    private String selectedAnswer;    // "India"
}