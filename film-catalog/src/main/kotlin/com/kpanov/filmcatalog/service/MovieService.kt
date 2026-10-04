package com.kpanov.filmcatalog.service

import com.kpanov.filmcatalog.repository.entity.Movie
import com.kpanov.filmcatalog.repository.MovieRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class MovieService(
    private val movieRepository: MovieRepository,
    private val directorService: DirectorService
) {
    fun getMovies(
        title: String?,
        minimalRating: Float?
    ) : List<Movie> {
        minimalRating?.let {
            require(it < 0f || it > 10f) { "Rating must be between 0 and 10." }
        }

        return movieRepository.findMoviesByTitleContainingAndRatingIsGreaterThanEqual(
            title ?: "",
            minimalRating ?: 0f
        )
    }

    fun getDirectorMovies(directorId: Long) : List<Movie>
        = movieRepository.findMoviesByDirectorId(directorId)

    @Transactional
    fun createMovie(
        title: String,
        year: Int,
        rating: Float? = null,
        directorId: Long
    ) {
        val director = directorService.getDirectorById(directorId)

        val movieRating = rating ?: (Random.nextFloat() * 10)

        val movie = Movie(
            title = title,
            releaseYear = year,
            rating = movieRating,
            director = director
        )

        movieRepository.save(movie)
    }
}