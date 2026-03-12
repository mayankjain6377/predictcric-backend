package com.sportsprediction.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CricApiService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    @Value("${rapidapi.base-url}")
    private String baseUrl;

    private final WebClient webClient;

    public CricApiService() {
        this.webClient = WebClient.builder().build();
    }

    // Fetch upcoming matches schedule
    public List<Map<String, Object>> getUpcomingMatches() {
        try {
            Map response = webClient.get()
                    .uri(baseUrl + "/cricket-schedule")
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", apiHost)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return extractMatchList(response);

        } catch (Exception e) {
            log.error("❌ Failed to fetch schedule: {}", e.getMessage());
        }
        return List.of();
    }

    // Fetch live/completed match score
    public Map<String, Object> getMatchScore(String matchId) {
        try {
            Map response = webClient.get()
                    .uri(baseUrl + "/cricket-match-info2?matchid=" + matchId)
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", apiHost)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) return response;

        } catch (Exception e) {
            log.error("❌ Failed to fetch match score: {}", e.getMessage());
        }
        return Map.of();
    }

    // Parse deeply nested Cricbuzz JSON structure
    private List<Map<String, Object>> extractMatchList(Map response) {
        List<Map<String, Object>> allMatches = new ArrayList<>();

        try {
            Map<String, Object> responseObj =
                    (Map<String, Object>) response.get("response");

            List<Map<String, Object>> schedules =
                    (List<Map<String, Object>>) responseObj.get("schedules");

            for (Map<String, Object> schedule : schedules) {
                Map<String, Object> wrapper =
                        (Map<String, Object>) schedule.get("scheduleAdWrapper");

                List<Map<String, Object>> matchScheduleList =
                        (List<Map<String, Object>>) wrapper.get("matchScheduleList");

                for (Map<String, Object> matchSchedule : matchScheduleList) {
                    List<Map<String, Object>> matchInfoList =
                            (List<Map<String, Object>>) matchSchedule.get("matchInfo");

                    if (matchInfoList != null) {
                        allMatches.addAll(matchInfoList);
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ Failed to parse match list: {}", e.getMessage());
        }

        return allMatches;
    }
}