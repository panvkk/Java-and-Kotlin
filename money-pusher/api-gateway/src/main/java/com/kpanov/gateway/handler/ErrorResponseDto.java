package com.kpanov.gateway.handler;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        LocalDateTime timestamp,
        String error,
        String message
) { }
