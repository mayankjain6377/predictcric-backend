package com.sportsprediction.app.service;

import com.sportsprediction.app.dto.request.ChallengeRequest;
import com.sportsprediction.app.dto.response.ChallengeResponse;
import com.sportsprediction.app.model.Challenge;
import com.sportsprediction.app.model.Match;
import com.sportsprediction.app.repository.ChallengeRepository;
import com.sportsprediction.app.repository.MatchRepository;
import com.sportsprediction.app.repository.PlayerRepository;
import com.sportsprediction.app.util.ChallengeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;


    @Value("${app.base-url}")
    private String baseUrl;


    public ChallengeResponse createChallenge(ChallengeRequest request) {

        // 1. Check both players exist
        playerRepository.findByPlayerId(request.getChallengerId())
                .orElseThrow(() -> new RuntimeException(
                        "Challenger not found: " + request.getChallengerId()));


        // 2. Can't challenge yourself
        if (request.getChallengerId().equals(request.getOpponentId())) {
            throw new RuntimeException("You cannot challenge yourself");
        }

        // 3. Check match exists
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // 4. Check challenge doesn't already exist between these two for this match
        challengeRepository.findByChallengerIdAndOpponentIdAndMatchId(
                        request.getChallengerId(),
                        request.getOpponentId(),
                        request.getMatchId())
                .ifPresent(c -> { throw new RuntimeException("Challenge already exists"); });

        // 5. Create and save challenge
        Challenge challenge = new Challenge();
        challenge.setMatch(match);
        challenge.setChallengerId(request.getChallengerId());
        challenge.setOpponentId(request.getOpponentId());

        challengeRepository.save(challenge);

        return toChallengeResponse(challenge);
    }

    // Opponent accepts the challenge
    public ChallengeResponse acceptChallenge(Long challengeId, String opponentId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // Only the opponent can accept
        if (!challenge.getOpponentId().equals(opponentId)) {
            throw new RuntimeException("Only the opponent can accept this challenge");
        }

        if (challenge.getStatus() != ChallengeStatus.PENDING) {
            throw new RuntimeException("Challenge is not in PENDING state");
        }

        challenge.setStatus(ChallengeStatus.ACCEPTED);
        challengeRepository.save(challenge);

        return toChallengeResponse(challenge);
    }

    // Get all challenges for a player
    public List<ChallengeResponse> getMyChallenges(String playerId) {
        return challengeRepository
                .findByChallengerIdOrOpponentId(playerId, playerId)
                .stream()
                .map(this::toChallengeResponse)
                .collect(Collectors.toList());
    }

    // Get single challenge by ID
    public ChallengeResponse getChallengeById(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        return toChallengeResponse(challenge);
    }

    // Helper — builds share link automatically
    private ChallengeResponse toChallengeResponse(Challenge c) {
        ChallengeResponse res = new ChallengeResponse();
        res.setChallengeId(c.getId());
        res.setChallengerId(c.getChallengerId());
        res.setOpponentId(c.getOpponentId());
        res.setMatchId(c.getMatch().getId());        // ← this line must exist
        res.setMatchTitle(c.getMatch().getMatchTitle());
        res.setStatus(c.getStatus());
        res.setWinnerId(c.getWinnerId());
        res.setShareLink(baseUrl + "/join-challenge/" + c.getId());
        return res;
    }
    public List<ChallengeResponse> getAllChallenges() {
        return challengeRepository.findAll()
                .stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .map(this::toChallengeResponse)
                .collect(Collectors.toList());
    }

    public void deleteChallenge(Long challengeId) {
        challengeRepository.deleteById(challengeId);
    }

    public void resetChallenge(Long challengeId) {
        Challenge c = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        c.setStatus(ChallengeStatus.ACCEPTED);
        c.setWinnerId(null);
        challengeRepository.save(c);
    }

    public void setWinner(Long challengeId, String winnerId) {
        Challenge c = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        c.setWinnerId(winnerId);
        c.setStatus(ChallengeStatus.COMPLETED);
        challengeRepository.save(c);
    }

}