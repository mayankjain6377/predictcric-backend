package com.sportsprediction.app.dto.response;

import com.sportsprediction.app.util.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChallengeResponse {
    private Long challengeId;
    private String challengerId;
    private String opponentId;
    private String matchTitle;
    private Long matchId;
    private ChallengeStatus status;
    private String winnerId;        // null until match ends
    private String shareLink;       // link to send to friend

    public ChallengeResponse() {

    }
}