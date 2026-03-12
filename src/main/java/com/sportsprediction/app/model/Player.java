package com.sportsprediction.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "players")
@Data
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String playerId;   // example: #CHIKA2847

    @Column
    private int coins = 0;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}