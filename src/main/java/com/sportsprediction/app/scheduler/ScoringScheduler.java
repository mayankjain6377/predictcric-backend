package com.sportsprediction.app.scheduler;

import com.sportsprediction.app.model.Match;
import com.sportsprediction.app.repository.MatchRepository;
import com.sportsprediction.app.service.ScoringService;
import com.sportsprediction.app.util.MatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoringScheduler {

    private final MatchRepository matchRepository;
    private final ScoringService scoringService;

    // Runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void autoScore() {

        LocalDateTime now = LocalDateTime.now();

        // Find LIVE matches that ended more than 4 hours ago
        // (assuming T20 match = ~4 hours)
        List<Match> matchesToScore = matchRepository
                .findByStatus(MatchStatus.LIVE)
                .stream()
                .filter(m -> m.getStartTime().plusHours(4).isBefore(now))
                .toList();

        if (matchesToScore.isEmpty()) return;

        for (Match match : matchesToScore) {
            try {
                scoringService.scoreMatch(match.getId());
                log.info("✅ Auto scored match: {} vs {}",
                        match.getTeamA(), match.getTeamB());
            } catch (Exception e) {
                log.error("❌ Failed to score match {}: {}",
                        match.getId(), e.getMessage());
            }
        }
    }
}