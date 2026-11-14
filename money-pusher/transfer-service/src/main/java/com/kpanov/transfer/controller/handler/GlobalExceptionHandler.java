package com.kpanov.transfer.controller.handler;

import com.kpanov.common.exception.ValidationException;
import com.kpanov.common.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleException(ValidationException e) {
        var error = "Bad request.";
        var message = e.getMessage();
        var timestamp = LocalDateTime.now(clock);
        var response = new ErrorResponseDto(
                timestamp, error, message
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleException(EntityNotFoundException e) {
        var error = "Not found.";
        var message = e.getMessage();
        var timestamp = LocalDateTime.now(clock);
        var response = new ErrorResponseDto(
                timestamp, error, message
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtException(DataIntegrityViolationException e) {
        var error = "Conflict.";
        var message = Messages.USER_ALREADY_EXISTS;
        var timestamp = LocalDateTime.now(clock);
        var response = new ErrorResponseDto(
                timestamp, error, message
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}