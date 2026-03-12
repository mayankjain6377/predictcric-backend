package com.sportsprediction.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Data
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private String playerId;      // #CHIKA2847

    @Column(nullable = false)
    private String selectedAnswer; // "India"

    @Column
    private Boolean isCorrect;    // filled after match ends

    @Column
    private LocalDateTime submittedAt = LocalDateTime.now();
}