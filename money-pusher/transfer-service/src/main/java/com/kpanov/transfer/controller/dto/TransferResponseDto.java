package com.kpanov.transfer.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kpanov.transfer.repository.entity.TransferStatus;

import java.time.Instant;

public record TransferResponseDto(
        @JsonProperty("transfer_id") Long transferId,
        @JsonProperty("transfer_amount") Long transferAmount,
        @JsonProperty("initiator_email") String initiatorEmail,
        @JsonProperty("target_email") String targetEmail,
        @JsonProperty("additional_message") String additionalMessage,
        TransferStatus status,
        @JsonProperty("created_at") Instant createdAt
) { }