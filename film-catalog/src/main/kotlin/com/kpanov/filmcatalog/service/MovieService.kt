package com.kpanov.filmcatalog.service

import com.kpanov.filmcatalog.repository.entity.MovieEntity
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
    ) : List<MovieEntity> {
        minimalRating?.let {
            require(it < 0f || it > 10f) { "Rating must be between 0 and 10." }
        }

        return movieRepository.findMoviesByTitleContainingAndRatingIsGreaterThanEqual(
            title ?: "",
            minimalRating ?: 0f
        )
    }

    fun getDirectorMovies(directorId: Long) : List<MovieEntity>
        = movieRepository.findMoviesByDirectorId(directorId)

    @Transactional
    fun createMovie(
        title: String,
        year: Int,
        rating: Float? = null,
        directorId: Long
    ) : MovieEntity {
        val director = directorService.getDirectorById(directorId)

        val movieRating = rating ?: (Random.nextFloat() * 10)

        val movieEntity = MovieEntity(
            title = title,
            releaseYear = year,
            rating = movieRating,
            director = director
        )

        return movieRepository.save(movieEntity)
    }
}