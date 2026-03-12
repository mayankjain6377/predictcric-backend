package com.sportsprediction.app.service;

import com.sportsprediction.app.dto.response.LeaderboardResponse;
import com.sportsprediction.app.model.Player;
import com.sportsprediction.app.repository.ChallengeRepository;
import com.sportsprediction.app.repository.PredictionRepository;
import com.sportsprediction.app.repository.PlayerRepository;
import com.sportsprediction.app.util.ChallengeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final PlayerRepository playerRepository;
    private final PredictionRepository predictionRepository;
    private final ChallengeRepository challengeRepository;

    public List<LeaderboardResponse> getLeaderboard() {
        List<Player> players = playerRepository.findAll();

        AtomicInteger rank = new AtomicInteger(1);

        return players.stream()
                .sorted((a, b) -> Integer.compare(b.getCoins(), a.getCoins()))
                .map(player -> {
                    LeaderboardResponse res = new LeaderboardResponse();
                    res.setRank(rank.getAndIncrement());
                    res.setPlayerId(player.getPlayerId());
                    res.setPlayerName(player.getName());
                    res.setCoins(player.getCoins());

                    // Total correct predictions
                    int totalCorrect = (int) predictionRepository
                            .findAll()
                            .stream()
                            .filter(p -> p.getPlayerId().equals(player.getPlayerId())
                                    && Boolean.TRUE.equals(p.getIsCorrect()))
                            .count();
                    res.setTotalCorrect(totalCorrect);

                    // Challenges won
                    long challengesWon = challengeRepository
                            .findByChallengerIdOrOpponentId(
                                    player.getPlayerId(), player.getPlayerId())
                            .stream()
                            .filter(c -> c.getStatus() == ChallengeStatus.COMPLETED
                                    && player.getPlayerId().equals(c.getWinnerId()))
                            .count();
                    res.setChallengesWon((int) challengesWon);

                    return res;
                })
                .collect(Collectors.toList());
    }
}