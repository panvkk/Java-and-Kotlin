package org.example.service

import org.example.entity.Director
import org.example.entity.Movie
import org.example.repository.MovieRepository
import org.springframework.stereotype.Service

@Service
class MovieService(
    private val movieRepository: MovieRepository
) {
    fun getMoviesByTitle(title: String?) : List<Movie>
        = movieRepository.findMoviesByTitle(title ?: "")

    fun getDirectorMovies(directorId: Long) : List<Movie>
        = movieRepository.findMoviesByDirectorId(directorId)

    fun getMoviesByRating(rating: Float) : List<Movie> {
        require(rating < 0f || rating > 10f) { "Rating must be between 0 and 10." }

        return movieRepository.findMoviesByRatingIsGreaterThanEqual(rating)
    }

    fun createMovie(
        title: String,
        year: Int,
        rating: Float?,
        director: Director
    )
}