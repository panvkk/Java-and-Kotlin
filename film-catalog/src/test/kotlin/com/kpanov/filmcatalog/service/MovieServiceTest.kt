package com.kpanov.filmcatalog.service

import com.kpanov.filmcatalog.repository.DirectorRepository
import com.kpanov.filmcatalog.repository.MovieRepository
import com.kpanov.filmcatalog.service.model.UpdateMovieModel
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MovieServiceTest {

    private val movieRepository: MovieRepository = mockk<MovieRepository>()
    private val directorRepository: DirectorRepository = mockk<DirectorRepository>()
    private val directorService = DirectorService(directorRepository)

    private val service = MovieService(movieRepository, directorService)

    @Test
    fun `GIVEN get movies request WHEN invalid minimal rating THEN throw exception`() {
        val title = "Title"
        val minimalRating = 20f

        // THEN
        assertThrows<IllegalArgumentException> {
            service.getMovies(title, minimalRating)
        }
    }

    @Test
    fun `GIVEN create movie request WHEN invalid release year THEN throw exception`() {
        // GIVEN
        val title = "Title"
        val invalidYear = -10
        val directorId = 100L

        // THEN
        assertThrows<IllegalArgumentException> {
            service.createMovie(title, invalidYear, directorId = directorId)
        }
    }

    @Test
    fun `GIVEN update movie request WHEN invalid release year THEN throw exception`() {
        // GIVEN
        val movieId = 1L
        val newMovie = UpdateMovieModel(
            title = "Title",
            year = -10,
            rating = 3.1f
        )

        // THEN
        assertThrows<IllegalArgumentException> {
            service.updateMovie(movieId, newMovie)
        }
    }
}