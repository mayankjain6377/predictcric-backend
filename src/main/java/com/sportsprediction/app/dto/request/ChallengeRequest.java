package com.sportsprediction.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChallengeRequest {

    @NotBlank(message = "Challenger ID is required")
    private String challengerId;    // #CHIKA2847

    @NotBlank(message = "Opponent ID is required")
    private String opponentId;      // #PRIYA5291

    @NotNull(message = "Match ID is required")
    private Long matchId;
}