package com.sportsprediction.app.service;

import com.sportsprediction.app.model.*;
import com.sportsprediction.app.repository.*;
import com.sportsprediction.app.util.ChallengeStatus;
import com.sportsprediction.app.util.MatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {

    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final QuestionRepository questionRepository;
    private final ChallengeRepository challengeRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public void scoreMatch(Long matchId) {

        // 1. Get match
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // 2. Mark match COMPLETED
        match.setStatus(MatchStatus.COMPLETED);
        matchRepository.save(match);

        // 3. Get all questions for this match
        List<Question> questions = questionRepository.findByMatchId(matchId);

        // 4. Get all predictions for this match
        List<Prediction> predictions = predictionRepository.findByMatchId(matchId);

        // 5. Mark each prediction correct/wrong + award 10 coins per correct
        for (Prediction prediction : predictions) {
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(prediction.getQuestion().getId()))
                    .findFirst()
                    .orElse(null);

            if (question == null || question.getCorrectAnswer() == null) continue;

            boolean isCorrect = question.getCorrectAnswer()
                    .equalsIgnoreCase(prediction.getSelectedAnswer());

            prediction.setIsCorrect(isCorrect);
            predictionRepository.save(prediction);

            // Award 10 coins per correct prediction to every player
            if (isCorrect) {
                playerRepository.findByPlayerId(prediction.getPlayerId())
                        .ifPresent(player -> {
                            player.setCoins(player.getCoins() + 10);
                            playerRepository.save(player);
                            log.info("✅ +10 coins → {} for correct prediction", player.getPlayerId());
                        });
            }
        }

        // 6. Score all challenges for this match
        scoreChallenges(matchId);

        log.info("✅ Match {} scored successfully", matchId);
    }

    // ── Score all challenges for a match ──────────────────────
    private void scoreChallenges(Long matchId) {
        List<Challenge> challenges = challengeRepository.findByStatus(ChallengeStatus.ACCEPTED)
                .stream()
                .filter(c -> c.getMatch().getId().equals(matchId))
                .toList();

        for (Challenge challenge : challenges) {
            String challengerId = challenge.getChallengerId();
            String opponentId  = challenge.getOpponentId();

            // Count correct predictions for each player
            int challengerScore = countCorrect(matchId, challengerId);
            int opponentScore   = countCorrect(matchId, opponentId);

            log.info("Challenge {} → {} scored {} | {} scored {}",
                    challenge.getId(), challengerId, challengerScore, opponentId, opponentScore);

            // Determine winner
            if (challengerScore > opponentScore) {
                // Challenger wins → +50 bonus coins
                challenge.setWinnerId(challengerId);
                awardBonus(challengerId, 50);
                log.info("🏆 Winner: {} → +50 bonus coins", challengerId);

            } else if (opponentScore > challengerScore) {
                // Opponent wins → +50 bonus coins
                challenge.setWinnerId(opponentId);
                awardBonus(opponentId, 50);
                log.info("🏆 Winner: {} → +50 bonus coins", opponentId);

            } else {
                // Draw — no bonus for anyone
                challenge.setWinnerId("DRAW");
                log.info("🤝 Draw — no bonus coins awarded");
            }

            challenge.setStatus(ChallengeStatus.COMPLETED);
            challengeRepository.save(challenge);
        }
    }

    // ── Count correct predictions for a player in a match ─────
    private int countCorrect(Long matchId, String playerId) {
        return (int) predictionRepository
                .findByPlayerIdAndMatchId(playerId, matchId)
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsCorrect()))
                .count();
    }

    // ── Award bonus coins to winner ───────────────────────────
    private void awardBonus(String playerId, int bonus) {
        playerRepository.findByPlayerId(playerId)
                .ifPresent(player -> {
                    int current = player.getCoins();
                    player.setCoins(current + bonus);
                    playerRepository.save(player);
                    log.info("🏆 Bonus +{} coins → {} (total: {})",
                            bonus, playerId, current + bonus);
                });
    }

    // ── Get player score for a match ──────────────────────────
    public int getPlayerScore(Long matchId, String playerId) {
        return countCorrect(matchId, playerId);
    }
}