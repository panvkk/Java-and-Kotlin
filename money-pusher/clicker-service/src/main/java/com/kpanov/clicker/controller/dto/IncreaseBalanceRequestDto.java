package com.kpanov.clicker.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kpanov.common.util.Constants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public record IncreaseBalanceRequestDto(
        @JsonProperty("balance_increase")
        @Max(value = Constants.MAX_ONCE_BALANCE_INCREASE)
        @NotNull Long balanceIncrease
) { }
