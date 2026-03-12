package com.sportsprediction.app.service;

import com.sportsprediction.app.model.Match;
import com.sportsprediction.app.model.Question;
import com.sportsprediction.app.repository.MatchRepository;
import com.sportsprediction.app.repository.QuestionRepository;
import com.sportsprediction.app.util.MatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.sportsprediction.app.dto.request.QuestionRequest;
import com.sportsprediction.app.dto.response.QuestionResponse;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchSyncService {

    private final CricApiService cricApiService;
    private final MatchRepository matchRepository;
    private final QuestionRepository questionRepository;
    private final ScoringService scoringService;

    // Sync real T20 matches from Cricbuzz into DB
    public String syncMatches() {
        List<Map<String, Object>> apiMatches = cricApiService.getUpcomingMatches();

        if (apiMatches.isEmpty()) return "No matches found from CricAPI";

        int count = 0;

        for (Map<String, Object> apiMatch : apiMatches) {
            try {
                String matchFormat = (String) apiMatch.get("matchFormat");

                // Only sync T20 matches
                if (matchFormat == null || !matchFormat.equalsIgnoreCase("T20")) continue;

                String cricApiId = String.valueOf(apiMatch.get("matchId"));

                // Skip if already exists in DB
                if (matchRepository.existsByCricApiId(cricApiId)) continue;

                // Extract team names
                Map<String, Object> team1 = (Map<String, Object>) apiMatch.get("team1");
                Map<String, Object> team2 = (Map<String, Object>) apiMatch.get("team2");
                String teamA = (String) team1.get("teamName");
                String teamB = (String) team2.get("teamName");

                // startDate comes as milliseconds timestamp → convert to LocalDateTime
                Long startDateMs = Long.valueOf(String.valueOf(apiMatch.get("startDate")));
                LocalDateTime startTime = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(startDateMs),
                        java.time.ZoneId.systemDefault());

                // Save match to DB
                Match match = new Match();
                match.setTeamA(teamA);
                match.setTeamB(teamB);
                match.setMatchTitle(teamA + " vs " + teamB);
                match.setStartTime(startTime);
                match.setStatus(MatchStatus.UPCOMING);
                match.setCricApiId(cricApiId);
                matchRepository.save(match);

                // Auto create 4 questions for this match
                createQuestionsForMatch(match, teamA, teamB);

                count++;
                log.info("✅ Synced: {} vs {}", teamA, teamB);

            } catch (Exception e) {
                log.error("❌ Failed to sync match: {}", e.getMessage());
            }
        }

        return "Synced " + count + " new T20 matches";
    }


//    set correct answer for a question (admin can do this manually if needed, e.g. for non-API matches or if API data is delayed)


    public void setCorrectAnswer(Long questionId, String answer) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setCorrectAnswer(answer);
        questionRepository.save(question);
    }


    // After match ends → fill correct answers → trigger scoring
    public String fillCorrectAnswers(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (match.getCricApiId() == null) {
            return "No CricAPI ID — set answers manually";
        }

        Map<String, Object> result = cricApiService.getMatchScore(match.getCricApiId());
        if (result.isEmpty()) return "No result data available yet";

        List<Question> questions = questionRepository.findByMatchId(matchId);

        String matchWinner = extractMatchWinner(result);
        String tossWinner = extractTossWinner(result);

        for (Question question : questions) {
            String text = question.getQuestionText().toLowerCase();

            if (text.contains("toss") && tossWinner != null) {
                question.setCorrectAnswer(tossWinner);
            } else if (text.contains("wins the match") && matchWinner != null) {
                question.setCorrectAnswer(matchWinner);
            }
            questionRepository.save(question);
        }

        // Trigger auto scoring
        scoringService.scoreMatch(matchId);
        return "Answers filled and scoring triggered for match: " + matchId;
    }

    // Auto generate 4 standard questions per match
    private void createQuestionsForMatch(Match match, String teamA, String teamB) {
        if (!questionRepository.findByMatchId(match.getId()).isEmpty()) return;

        List<Question> questions = List.of(
                createQuestion(match, "Who wins the toss?",
                        teamA, teamB, "No Toss", "Tie"),
                createQuestion(match, "Who wins the match?",
                        teamA, teamB, "Draw", "No Result"),
                createQuestion(match, "Total runs scored?",
                        "150-175", "176-200", "201-225", "225+"),
                createQuestion(match, "Who scores highest runs?",
                        teamA + " Batsman", teamB + " Batsman", "Draw", "No Result")
        );
        questionRepository.saveAll(questions);
    }
    public String createDefaultQuestionsForMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Delete existing first
        List<Question> existing = questionRepository.findByMatchId(matchId);
        questionRepository.deleteAll(existing);

        // ✅ Create all 4 default questions
        List<Question> questions = List.of(
                createQuestion(match,
                        "Who wins the toss?",
                        match.getTeamA(), match.getTeamB(), "No Toss", "Tie"),
                createQuestion(match,
                        "Who wins the match?",
                        match.getTeamA(), match.getTeamB(), "Draw", "No Result"),
                createQuestion(match,
                        "Total runs scored?",
                        "150-175", "176-200", "201-225", "225+"),
                createQuestion(match,
                        "Who scores highest runs?",
                        match.getTeamA() + " Batsman", match.getTeamB() + " Batsman", "Draw", "No Result")
        );
        questionRepository.saveAll(questions);

        return "Created 4 default questions for: " + match.getMatchTitle();
    }
    private Question createQuestion(Match match, String text,
                                    String a, String b, String c, String d) {
        Question q = new Question();
        q.setMatch(match);
        q.setQuestionText(text);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        return q;
    }

    private String extractMatchWinner(Map<String, Object> result) {
        try {
            return (String) result.get("matchWinner");
        } catch (Exception e) { return null; }
    }

    private String extractTossWinner(Map<String, Object> result) {
        try {
            Map<String, Object> toss = (Map<String, Object>) result.get("tossResults");
            return toss != null ? (String) toss.get("tossWinner") : null;
        } catch (Exception e) { return null; }
    }

    // ── Add new question to match ────────────────────────────
    public QuestionResponse addQuestion(Long matchId, QuestionRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        Question q = new Question();
        q.setMatch(match);
        q.setQuestionText(request.getQuestionText());
        q.setOptionA(request.getOptionA());
        q.setOptionB(request.getOptionB());
        q.setOptionC(request.getOptionC());
        q.setOptionD(request.getOptionD());
        if (request.getCorrectAnswer() != null) {
            q.setCorrectAnswer(request.getCorrectAnswer());
        }
        questionRepository.save(q);
        return toQuestionResponse(q);
    }

    // ── Delete question ──────────────────────────────────────
    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    // ── Update question ──────────────────────────────────────
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (request.getQuestionText() != null) q.setQuestionText(request.getQuestionText());
        if (request.getOptionA() != null) q.setOptionA(request.getOptionA());
        if (request.getOptionB() != null) q.setOptionB(request.getOptionB());
        if (request.getOptionC() != null) q.setOptionC(request.getOptionC());
        if (request.getOptionD() != null) q.setOptionD(request.getOptionD());
        if (request.getCorrectAnswer() != null) q.setCorrectAnswer(request.getCorrectAnswer());

        questionRepository.save(q);
        return toQuestionResponse(q);
    }


    // ── Helper ───────────────────────────────────────────────
    private QuestionResponse toQuestionResponse(Question q) {
        QuestionResponse res = new QuestionResponse();
        res.setId(q.getId());
        res.setQuestionText(q.getQuestionText());
        res.setOptionA(q.getOptionA());
        res.setOptionB(q.getOptionB());
        res.setOptionC(q.getOptionC());
        res.setOptionD(q.getOptionD());
        res.setCorrectAnswer(q.getCorrectAnswer());
        return res;
    }
}