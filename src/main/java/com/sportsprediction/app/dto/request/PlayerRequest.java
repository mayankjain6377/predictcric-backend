package com.sportsprediction.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlayerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    // optional — if null, system generates one
    private String preferredId;
}