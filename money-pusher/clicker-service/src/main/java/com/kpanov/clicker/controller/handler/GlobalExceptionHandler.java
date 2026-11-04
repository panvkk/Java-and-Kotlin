package com.kpanov.clicker.controller.handler;

import com.kpanov.clicker.exception.BalanceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    public final Clock clock;

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleException(BalanceNotFoundException e) {
        var error = "Bad Request.";
        var message = e.getMessage();
        var timestamp = LocalDateTime.now(clock);

        var response = new ErrorResponseDto(
                timestamp, error, message
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
