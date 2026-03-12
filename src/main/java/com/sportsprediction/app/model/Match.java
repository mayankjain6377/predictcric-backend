package com.sportsprediction.app.model;

import com.sportsprediction.app.util.MatchStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teama", nullable = false)
    private String teamA;

    @Column(name = "teamb", nullable = false)
    private String teamB;

    @Column(unique = true)
    private String cricApiId;   // links to real CricAPI match

    @Column(nullable = false)
    private String matchTitle;   // "ICC T20 World Cup Final"

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.UPCOMING;
}