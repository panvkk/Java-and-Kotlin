package com.kpanov.auth.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponseDto(
        @JsonProperty("access_token")
        String accessToken
) { }