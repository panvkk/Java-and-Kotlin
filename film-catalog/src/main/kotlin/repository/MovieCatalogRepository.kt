package org.example.repository

import org.example.entity.Director
import org.example.entity.Movie
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MovieCatalogRepository : JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m JOIN m.director d WHERE d = :director")
    fun findByDirectorMovies(director: Director): List<Movie>

    @Query("SELECT d FROM Director d WHERE d.fullName = :directorName")
    fun findDirectorByName(directorName: String) : Director?
}