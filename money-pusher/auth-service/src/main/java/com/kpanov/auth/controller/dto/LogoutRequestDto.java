package com.kpanov.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LogoutRequestDto(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken
) { }