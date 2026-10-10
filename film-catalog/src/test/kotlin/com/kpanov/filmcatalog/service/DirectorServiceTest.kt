package com.kpanov.filmcatalog.service

import com.kpanov.filmcatalog.repository.DirectorRepository
import com.kpanov.filmcatalog.repository.entity.DirectorEntity
import com.kpanov.filmcatalog.service.model.UpdateDirectorModel
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DirectorServiceTest {

    private val repository: DirectorRepository = mockk<DirectorRepository>()

    private val service: DirectorService = DirectorService(repository)

    @Test
    fun `GIVEN request WHEN director not found THEN throw exception`() {
        // GIVEN
        val directorId = 100L
        every { repository.findDirectorById(directorId) } returns null

        // THEN
        assertThrows<EntityNotFoundException> {
            service.getDirectorById(directorId)
        }
    }

    @Test
    fun `GIVEN create director request WHEN invalid data THEN throw exception`() {
        // GIVEN
        val fullName = "Director"
        val birthYear = -10

        // THEN
        assertThrows<IllegalArgumentException> {
            service.createDirector(fullName, birthYear)
        }
    }

    @Test
    fun `GIVEN update director request WHEN invalid data THEN throw exception`() {
        // GIVEN
        val directorId = 100L
        val newDirector = UpdateDirectorModel(
            fullName = "Director",
            birthYear = -10
        )

        // THEN
        assertThrows<IllegalArgumentException> {
            service.updateDirector(directorId, newDirector)
        }
    }

    @Test
    fun `GIVEN create director request WHEN valid data THEN return created director`() {
        // GIVEN
        val fullName = "Director"
        val birthYear = 1980
        val expectedDirectorEntity = DirectorEntity(fullName = fullName, birthYear = birthYear)

        // WHEN
        every { repository.save(expectedDirectorEntity) } returns expectedDirectorEntity
        val actualDirectorEntity = service.createDirector(fullName, birthYear)

        // THEN
        assertEquals(expectedDirectorEntity, actualDirectorEntity)
    }
}