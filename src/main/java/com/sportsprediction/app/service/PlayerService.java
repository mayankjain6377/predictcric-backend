package com.sportsprediction.app.service;

import com.sportsprediction.app.dto.request.PlayerRequest;
import com.sportsprediction.app.dto.response.PlayerResponse;
import com.sportsprediction.app.model.Player;
import com.sportsprediction.app.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    // New player joins
    public PlayerResponse joinAsPlayer(PlayerRequest request) {

        String playerId;

        if (request.getPreferredId() != null && !request.getPreferredId().isBlank()) {
            // Player chose their own ID
            playerId = request.getPreferredId().toUpperCase();

            // Check if already taken
            if (playerRepository.existsByPlayerId(playerId)) {
                throw new RuntimeException("Player ID already taken: " + playerId);
            }
        } else {
            // System generates a suggested ID
            playerId = generatePlayerId(request.getName());
        }

        Player player = new Player();
        player.setName(request.getName());
        player.setPlayerId(playerId);

        playerRepository.save(player);

        return new PlayerResponse(playerId, player.getName(), player.getCoins());
    }

    // Returning player logs back in with their ID
    public PlayerResponse getPlayer(String playerId) {
        Player player = playerRepository.findByPlayerId(playerId.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Player not found: " + playerId));
        return new PlayerResponse(player.getPlayerId(), player.getName(), player.getCoins());
    }
    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getCoins(), a.getCoins()))
                .map(p -> new PlayerResponse(p.getPlayerId(), p.getName(), p.getCoins()))
                .collect(Collectors.toList());
    }

    public void deletePlayer(String playerId) {
        Player player = playerRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        playerRepository.delete(player);
    }

    // Suggest an ID based on name — player can edit before confirming
    public String suggestId(String name) {
        String prefix = name.length() > 5
                ? name.substring(0, 5).toUpperCase()
                : name.toUpperCase();

        String suggested;
        Random random = new Random();

        do {
            int number = 1000 + random.nextInt(9000);
            suggested = prefix + number;
        } while (playerRepository.existsByPlayerId(suggested));

        return suggested;
    }

    // Add coins after correct prediction
    public void addCoins(String playerId, int coins) {
        Player player = playerRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        player.setCoins(player.getCoins() + coins);
        playerRepository.save(player);
    }

    // Private helper
    private String generatePlayerId(String name) {
        return suggestId(name);
    }
}