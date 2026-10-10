package com.kpanov.filmcatalog.controller.handler

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val error: String,
    val message: String
)
