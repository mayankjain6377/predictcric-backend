package com.sportsprediction.app.model;

import com.sportsprediction.app.util.ChallengeStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
@Data
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private String challengerId;    // #CHIKA2847 — person who sent challenge

    @Column(nullable = false)
    private String opponentId;      // #PRIYA5291 — person who received challenge

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status = ChallengeStatus.PENDING;

    @Column
    private String winnerId;        // filled after match ends

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}