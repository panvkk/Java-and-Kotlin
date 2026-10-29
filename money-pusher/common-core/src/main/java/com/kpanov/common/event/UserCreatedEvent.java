package com.kpanov.common.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCreatedEvent(
        @JsonProperty("user_id")
        Integer userId,
        String nickname,
        String email
) { }