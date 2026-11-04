package com.kpanov.clicker.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LeaderboardResponseItemDto(
        Long balance,
        @JsonProperty("user_nickname")
        String userNickname,
        @JsonProperty("user_email")
        String userEmail
) { }