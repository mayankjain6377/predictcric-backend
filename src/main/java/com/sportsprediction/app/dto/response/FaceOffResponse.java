package com.sportsprediction.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class FaceOffResponse {

    private String player1Id;
    private String player1Name;
    private int player1Score;

    private String player2Id;
    private String player2Name;
    private int player2Score;

    private String winnerId;        // null if match not done yet
    private String matchTitle;

    private List<QuestionComparisonResponse> comparisons;
}