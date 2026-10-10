package com.kpanov.filmcatalog.repository

import com.kpanov.filmcatalog.repository.entity.MovieEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : JpaRepository<MovieEntity, Long> {

    fun findMoviesByDirectorId(directorId: Long): List<MovieEntity>

    fun findMovieById(movieId: Long): MovieEntity?

    fun findMoviesByTitleContainingAndRatingIsGreaterThanEqual(
        title: String,
        minimalRating: Float
    ): List<MovieEntity>

    fun deleteMovieById(id: Long)
}