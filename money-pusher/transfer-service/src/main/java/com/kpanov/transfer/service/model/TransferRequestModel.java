package com.kpanov.transfer.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferRequestModel(
        String initiatorUserEmail,
        String targetUserEmail,
        Long transferAmount,
        String additionalMessage
) { }
