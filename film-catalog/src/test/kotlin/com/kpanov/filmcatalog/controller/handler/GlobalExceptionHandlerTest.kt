package com.kpanov.filmcatalog.controller.handler

import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class GlobalExceptionHandlerTest {

    private var exceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    @Test
    fun `WHEN invalid rating THEN return bad request`() {
        val actualMessage = "Invalid rating."

        // WHEN
        val exception = IllegalArgumentException(actualMessage)
        val actualResponse = exceptionHandler.handle(exception)

        // THEN
        val expectedCode = HttpStatus.BAD_REQUEST
        assertEquals(expectedCode, actualResponse.statusCode)
        assertNotNull(actualResponse.body)
        assertEquals(actualMessage, actualResponse.body?.message)
    }

    @Test
    fun `WHEN entity not found THEN return not found`() {
        val actualMessage = "Entity not found."

        // WHEN
        val exception = EntityNotFoundException(actualMessage)
        val actualResponse = exceptionHandler.handle(exception)

        // THEN
        val expectedCode = HttpStatus.NOT_FOUND
        assertEquals(expectedCode, actualResponse.statusCode)
        assertNotNull(actualResponse.body)
        assertEquals(actualMessage, actualResponse.body?.message)
    }
}