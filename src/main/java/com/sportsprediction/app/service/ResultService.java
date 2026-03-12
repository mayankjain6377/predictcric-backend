package com.sportsprediction.app.service;

import com.sportsprediction.app.dto.response.FaceOffResponse;
import com.sportsprediction.app.dto.response.QuestionComparisonResponse;
import com.sportsprediction.app.model.Challenge;
import com.sportsprediction.app.model.Player;
import com.sportsprediction.app.model.Prediction;
import com.sportsprediction.app.model.Question;
import com.sportsprediction.app.repository.ChallengeRepository;
import com.sportsprediction.app.repository.PlayerRepository;
import com.sportsprediction.app.repository.PredictionRepository;
import com.sportsprediction.app.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ChallengeRepository challengeRepository;
    private final PredictionRepository predictionRepository;
    private final PlayerRepository playerRepository;
    private final QuestionRepository questionRepository;

    public FaceOffResponse getFaceOff(Long challengeId) {

        // 1. Get challenge
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        Long matchId = challenge.getMatch().getId();

        // 2. Get both players
        Player player1 = playerRepository.findByPlayerId(challenge.getChallengerId())
                .orElseThrow(() -> new RuntimeException("Player 1 not found"));

        Player player2 = playerRepository.findByPlayerId(challenge.getOpponentId())
                .orElseThrow(() -> new RuntimeException("Player 2 not found"));

        // 3. Get predictions for both players for this match
        Map<Long, Prediction> player1Predictions =
                predictionRepository.findByPlayerIdAndMatchId(
                                challenge.getChallengerId(), matchId)
                        .stream()
                        .collect(Collectors.toMap(
                                p -> p.getQuestion().getId(), p -> p));

        Map<Long, Prediction> player2Predictions =
                predictionRepository.findByPlayerIdAndMatchId(
                                challenge.getOpponentId(), matchId)
                        .stream()
                        .collect(Collectors.toMap(
                                p -> p.getQuestion().getId(), p -> p));

        // 4. Get all questions for this match
        List<Question> questions = questionRepository.findByMatchId(matchId);

        // 5. Build question-by-question comparison
        List<QuestionComparisonResponse> comparisons = new ArrayList<>();

        for (Question question : questions) {
            Prediction p1 = player1Predictions.get(question.getId());
            Prediction p2 = player2Predictions.get(question.getId());

            comparisons.add(new QuestionComparisonResponse(
                    question.getQuestionText(),
                    question.getCorrectAnswer(),
                    p1 != null ? p1.getSelectedAnswer() : "No answer",
                    p1 != null ? p1.getIsCorrect() : false,
                    p2 != null ? p2.getSelectedAnswer() : "No answer",
                    p2 != null ? p2.getIsCorrect() : false
            ));
        }

        // 6. Calculate scores
        int player1Score = (int) player1Predictions.values().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsCorrect()))
                .count();

        int player2Score = (int) player2Predictions.values().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsCorrect()))
                .count();

        return new FaceOffResponse(
                player1.getPlayerId(),
                player1.getName(),
                player1Score,
                player2.getPlayerId(),
                player2.getName(),
                player2Score,
                challenge.getWinnerId(),
                challenge.getMatch().getMatchTitle(),
                comparisons
        );
    }
}