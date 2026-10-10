package com.kpanov.filmcatalog.service

import com.kpanov.filmcatalog.repository.MovieRepository
import com.kpanov.filmcatalog.repository.entity.MovieEntity
import com.kpanov.filmcatalog.service.model.UpdateMovieModel
import com.kpanov.filmcatalog.util.INVALID_RATING_MESSAGE
import com.kpanov.filmcatalog.util.MAX_RATING
import com.kpanov.filmcatalog.util.MIN_RATING
import com.kpanov.filmcatalog.util.MIN_RELEASE_YEAR
import com.kpanov.filmcatalog.util.MOVIE_NOT_FOUND_BY_ID_MESSAGE
import com.kpanov.filmcatalog.util.RATING_GENERATION_FACTOR
import jakarta.persistence.EntityNotFoundException
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
            require(it in MIN_RATING..MAX_RATING) {
                INVALID_RATING_MESSAGE.format(it)
            }
        }

        return movieRepository.findMoviesByTitleContainingAndRatingIsGreaterThanEqual(
            title ?: "",
            minimalRating ?: MIN_RATING
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
        validateReleaseYear(year)

        val director = directorService.getDirectorById(directorId)

        val movieRating = rating ?: (Random.nextFloat() * RATING_GENERATION_FACTOR)

        val movieEntity = MovieEntity(
            title = title,
            releaseYear = year,
            rating = movieRating,
            director = director
        )

        return movieRepository.save(movieEntity)
    }

    @Transactional
    fun deleteMovie(id: Long) {
        movieRepository.deleteMovieById(id)
    }

    @Transactional
    fun updateMovie(
        movieId: Long,
        newMovie: UpdateMovieModel
    ) : MovieEntity {
        validateReleaseYear(newMovie.year)

        val currentMovieEntity = movieRepository.findMovieById(movieId)
            ?: throw EntityNotFoundException(MOVIE_NOT_FOUND_BY_ID_MESSAGE.format(movieId))

        val newMovieEntity = currentMovieEntity.copy(
            title = newMovie.title,
            rating = newMovie.rating,
            releaseYear = newMovie.year
        )

        return movieRepository.save(newMovieEntity)
    }

    private fun validateReleaseYear(releaseYear: Int) {
        require(releaseYear > MIN_RELEASE_YEAR) {
            INVALID_RATING_MESSAGE.format(releaseYear)
        }
    }
}