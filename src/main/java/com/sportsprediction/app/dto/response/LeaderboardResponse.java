package com.sportsprediction.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardResponse {
    private int rank;
    private String playerId;
    private String playerName;
    private int coins;
    private int totalCorrect;
    private int challengesWon;

    public LeaderboardResponse() {

    }
}