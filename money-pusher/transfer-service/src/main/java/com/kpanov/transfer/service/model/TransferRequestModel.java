package com.kpanov.transfer.service.model;

public record TransferRequestModel(
        String initiatorUserEmail,
        String targetUserEmail,
        Long transferAmount,
        String additionalMessage
) { }
