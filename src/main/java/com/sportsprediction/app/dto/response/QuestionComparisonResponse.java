package com.sportsprediction.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionComparisonResponse {
    private String questionText;
    private String correctAnswer;
    private String player1Answer;
    private Boolean player1Correct;
    private String player2Answer;
    private Boolean player2Correct;
}