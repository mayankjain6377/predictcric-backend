package com.sportsprediction.app.dto.response;

import com.sportsprediction.app.util.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MatchResponse {
    private Long id;
    private String teamA;
    private String teamB;
    private String matchTitle;
    private LocalDateTime startTime;
    private MatchStatus status;
}