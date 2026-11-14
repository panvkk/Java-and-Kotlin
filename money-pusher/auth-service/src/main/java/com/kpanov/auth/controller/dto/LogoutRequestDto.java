package com.kpanov.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record LogoutRequestDto(
        @JsonProperty("access_token")
        @NotBlank String accessToken,
        @JsonProperty("refresh_token")
        @NotBlank String refreshToken
) { }