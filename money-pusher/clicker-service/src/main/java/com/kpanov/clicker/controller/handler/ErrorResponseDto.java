package com.kpanov.clicker.controller.handler;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        LocalDateTime timestamp,
        String error,
        String message
) { }