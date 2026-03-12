package com.sportsprediction.app.scheduler;

import com.sportsprediction.app.model.Match;
import com.sportsprediction.app.repository.MatchRepository;
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
public class PredictionLockScheduler {

    private final MatchRepository matchRepository;

    // Runs every 60 seconds
    @Scheduled(fixedRate = 60000)
    public void lockPredictions() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusMinutes(5);

        // Find all UPCOMING matches starting within next 5 minutes
        List<Match> matchesToLock = matchRepository
                .findByStatus(MatchStatus.UPCOMING)
                .stream()
                .filter(m -> m.getStartTime().isBefore(cutoff))
                .toList();

        if (matchesToLock.isEmpty()) return;

        for (Match match : matchesToLock) {
            match.setStatus(MatchStatus.LIVE);
            matchRepository.save(match);
            log.info("🔒 Predictions locked for match: {} vs {}",
                    match.getTeamA(), match.getTeamB());
        }
    }
}