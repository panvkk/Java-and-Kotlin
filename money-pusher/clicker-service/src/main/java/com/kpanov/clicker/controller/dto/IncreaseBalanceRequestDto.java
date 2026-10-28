package com.kpanov.clicker.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IncreaseBalanceRequestDto(
        @JsonProperty("balance_increase")
        Long balanceIncrease
) { }
