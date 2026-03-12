package com.sportsprediction.app.util;

public enum ChallengeStatus {
    PENDING,    // challenger sent, opponent hasn't accepted yet
    ACCEPTED,   // opponent accepted, both are predicting
    COMPLETED   // match done, winner decided
}