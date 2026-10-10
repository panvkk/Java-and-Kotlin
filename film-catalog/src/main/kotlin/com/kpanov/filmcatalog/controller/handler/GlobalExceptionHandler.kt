package com.kpanov.filmcatalog.controller.handler

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Clock
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler(
    private val clock: Clock = Clock.systemDefaultZone()
) {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handle(e: IllegalArgumentException) : ResponseEntity<ErrorResponse>{
        val error = "Bad Request"
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(clock),
            status = HttpStatus.BAD_REQUEST.value(),
            error = error,
            message = e.message ?: error
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handle(e: EntityNotFoundException) : ResponseEntity<ErrorResponse>{
        val error = "Not Found"
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(clock),
            status = HttpStatus.NOT_FOUND.value(),
            error = error,
            message = e.message ?: error
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }
}