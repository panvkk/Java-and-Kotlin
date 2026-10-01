package org.example.repository

import org.example.entity.Movie
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : JpaRepository<Movie, Long> {

    fun findMoviesByDirectorId(directorId: Long): List<Movie>

    fun findMoviesByTitle(title: String): List<Movie>

    fun findMoviesByRatingIsGreaterThanEqual(minimalRating: Float): List<Movie>

    fun putMovie(movie: Movie)
}