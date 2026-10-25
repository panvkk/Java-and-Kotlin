package com.kpanov.clicker.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IncreaseBalanceRequestDto(
        @JsonProperty("user_id")
        Integer userId,
        @JsonProperty("balance_increase")
        Long balanceIncrease
) { }
