package com.sportsprediction.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "questions")
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private String questionText;    // "Who wins the toss?"

    @Column(name = "optiona", nullable = false)
    private String optionA;

    @Column(name = "optionb", nullable = false)
    private String optionB;

    @Column(name = "optionc")
    private String optionC;     // optional 3rd option

    @Column(name = "optiond")
    private String optionD;

    @Column(name = "correct_answer")
    private String correctAnswer;   // filled after match ends
}