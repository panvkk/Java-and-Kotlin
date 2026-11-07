package com.kpanov.common.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferRequestEvent(
        Long id,
        @JsonProperty("initiator_email")
        String initiatorEmail,
        @JsonProperty("target_email")
        String targetEmail,
        @JsonProperty("transfer_amount")
        Long transferAmount
) { }
