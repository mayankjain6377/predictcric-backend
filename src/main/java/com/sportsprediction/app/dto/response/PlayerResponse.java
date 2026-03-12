package com.sportsprediction.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerResponse {
    private String playerId;   // #CHIKA2847
    private String name;
    private int coins;
}