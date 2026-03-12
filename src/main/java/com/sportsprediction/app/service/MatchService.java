package com.sportsprediction.app.service;

import com.sportsprediction.app.dto.response.MatchResponse;
import com.sportsprediction.app.dto.response.QuestionResponse;
import com.sportsprediction.app.model.Match;
import com.sportsprediction.app.model.Question;
import com.sportsprediction.app.repository.MatchRepository;
import com.sportsprediction.app.repository.QuestionRepository;
import com.sportsprediction.app.util.MatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final QuestionRepository questionRepository;
    private final MatchSyncService matchSyncService;

    // Get all upcoming matches
    public List<MatchResponse> getUpcomingMatches() {
        return matchRepository.findByStatus(MatchStatus.UPCOMING)
                .stream()
                .map(this::toMatchResponse)
                .collect(Collectors.toList());
    }

    //Get all matches (for admin)
    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll()
                .stream()
                .map(this::toMatchResponse)
                .collect(Collectors.toList());
    }

    // Get single match by ID
    public MatchResponse getMatchById(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found: " + matchId));
        return toMatchResponse(match);
    }

    // Get all questions for a match
    public List<QuestionResponse> getQuestionsByMatch(Long matchId) {
        return questionRepository.findByMatchId(matchId)
                .stream()
                .map(q -> new QuestionResponse(
                        q.getId(),
                        q.getQuestionText(),
                        q.getOptionA(),
                        q.getOptionB(),
                        q.getOptionC(),
                        q.getOptionD()
                ))
                .collect(Collectors.toList());
    }
    public List<Question> getQuestionsByMatchId(Long matchId) {
        List<Question> questions = questionRepository.findByMatchId(matchId);

        if (questions.isEmpty()) {
            Match match = matchRepository.findById(matchId)
                    .orElseThrow(() -> new RuntimeException("Match not found"));
            matchSyncService.createDefaultQuestionsForMatch(matchId);
            questions = questionRepository.findByMatchId(matchId);
        }

        return questions;
    }

    // Helper
    private MatchResponse toMatchResponse(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getTeamA(),
                match.getTeamB(),
                match.getMatchTitle(),
                match.getStartTime(),
                match.getStatus()
        );
    }
}
