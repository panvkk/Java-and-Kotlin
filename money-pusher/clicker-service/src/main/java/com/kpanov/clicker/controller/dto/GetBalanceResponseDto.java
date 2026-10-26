package com.kpanov.clicker.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetBalanceResponseDto(
        @JsonProperty("user_balance")
        Long userBalance
) { }