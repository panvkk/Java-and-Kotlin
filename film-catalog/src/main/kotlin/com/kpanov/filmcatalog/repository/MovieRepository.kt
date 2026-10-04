package com.kpanov.filmcatalog.repository

import com.kpanov.filmcatalog.repository.entity.Movie
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : JpaRepository<Movie, Long> {

    fun findMoviesByDirectorId(directorId: Long): List<Movie>

    fun findMoviesByTitleContainingAndRatingIsGreaterThanEqual(
        title: String,
        minimalRating: Float
    ): List<Movie>

}