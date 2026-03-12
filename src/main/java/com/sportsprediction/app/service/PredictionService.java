package com.sportsprediction.app.service;

import com.sportsprediction.app.dto.request.PredictionRequest;
import com.sportsprediction.app.dto.response.PredictionResponse;
import com.sportsprediction.app.model.Match;
import com.sportsprediction.app.model.Player;
import com.sportsprediction.app.model.Prediction;
import com.sportsprediction.app.model.Question;
import com.sportsprediction.app.repository.MatchRepository;
import com.sportsprediction.app.repository.PlayerRepository;
import com.sportsprediction.app.repository.PredictionRepository;
import com.sportsprediction.app.repository.QuestionRepository;
import com.sportsprediction.app.util.MatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final MatchRepository matchRepository;
    private final QuestionRepository questionRepository;
    private final PlayerRepository playerRepository;

    public PredictionResponse submitPrediction(Long matchId, PredictionRequest request) {

        // 1. Check match exists
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // 2. Check match is still UPCOMING — can't predict on LIVE or COMPLETED
        if (match.getStatus() != MatchStatus.UPCOMING) {
            throw new RuntimeException("Predictions are locked for this match");
        }

        // 3. Check player exists
        playerRepository.findByPlayerId(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("Player not found: " + request.getPlayerId()));

        // 4. Check question exists and belongs to this match
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getMatch().getId().equals(matchId)) {
            throw new RuntimeException("Question does not belong to this match");
        }

        // 5. Check player hasn't already answered this question
        predictionRepository.findByPlayerIdAndQuestionId(
                        request.getPlayerId(), request.getQuestionId())
                .ifPresent(p -> { throw new RuntimeException("Already answered this question"); });

        // 6. Save prediction
        Prediction prediction = new Prediction();
        prediction.setMatch(match);
        prediction.setQuestion(question);
        prediction.setPlayerId(request.getPlayerId());
        prediction.setSelectedAnswer(request.getSelectedAnswer());

        predictionRepository.save(prediction);

        return toPredictionResponse(prediction);
    }

    // Get all predictions a player made for a match
    public List<PredictionResponse> getPlayerPredictions(String playerId, Long matchId) {
        return predictionRepository.findByPlayerIdAndMatchId(playerId, matchId)
                .stream()
                .map(this::toPredictionResponse)
                .collect(Collectors.toList());
    }
    public List<PredictionResponse> getPredictionsByMatch(Long matchId) {
        return predictionRepository.findByMatchId(matchId)
                .stream()
                .map(this::toPredictionResponse)
                .collect(Collectors.toList());
    }

    public List<PredictionResponse> getPredictionsByPlayer(String playerId) {
        return predictionRepository.findByPlayerId(playerId)
                .stream()
                .map(this::toPredictionResponse)
                .collect(Collectors.toList());
    }

    public void deletePrediction(Long predictionId) {
        predictionRepository.deleteById(predictionId);
    }

    // Helper
    private PredictionResponse toPredictionResponse(Prediction p) {
        PredictionResponse res = new PredictionResponse();
        res.setId(p.getId());
        res.setPlayerId(p.getPlayerId());
        res.setQuestionId(p.getQuestion().getId());
        res.setQuestionText(p.getQuestion().getQuestionText());
        res.setSelectedAnswer(p.getSelectedAnswer());
        res.setIsCorrect(p.getIsCorrect());
        return res;
    }
}