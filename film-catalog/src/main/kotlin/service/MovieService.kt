package org.example.service

import org.example.entity.Movie
import org.example.repository.MovieRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class MovieService(
    private val movieRepository: MovieRepository,
    private val directorService: DirectorService
) {
    fun getMoviesByTitle(title: String?) : List<Movie>
        = movieRepository.findMoviesByTitle(title ?: "")

    fun getDirectorMovies(directorId: Long) : List<Movie>
        = movieRepository.findMoviesByDirectorId(directorId)

    fun getMoviesByRating(rating: Float) : List<Movie> {
        require(rating < 0f || rating > 10f) { "Rating must be between 0 and 10." }

        return movieRepository.findMoviesByRatingIsGreaterThanEqual(rating)
    }

    @Transactional
    fun createMovie(
        title: String,
        year: Int,
        rating: Float?,
        directorId: Long
    ) {
        val director = directorService.getDirectorById(directorId)

        val movieRating = rating ?: (Random.nextFloat() * 10)

        val movie = Movie(
            title = title,
            year = year,
            rating = movieRating,
            director = director 
        )

        movieRepository.save(movie)
    }
}