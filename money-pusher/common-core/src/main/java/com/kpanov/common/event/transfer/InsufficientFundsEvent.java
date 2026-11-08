package com.kpanov.common.event.transfer;

public record InsufficientFundsEvent(
        Long transferId,
        String initiatorEmail
) { }
