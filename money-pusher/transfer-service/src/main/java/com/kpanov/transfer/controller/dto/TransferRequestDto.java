package com.kpanov.transfer.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferRequestDto(
        @JsonProperty("target_user_email")
        String targetUserEmail,
        @JsonProperty("transfer_amount")
        Long transferAmount,
        @JsonProperty("additional_message")
        String additionalMessage
) { }