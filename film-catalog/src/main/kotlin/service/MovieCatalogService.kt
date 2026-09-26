package org.example.service

import jakarta.transaction.Transactional
import org.example.entity.Director
import org.example.entity.Movie
import org.example.repository.MovieCatalogRepository
import org.springframework.stereotype.Service

@Service
class MovieCatalogService(
    private val repository: MovieCatalogRepository
) {
    fun getDirectorMovies(directorName: Director) : List<Movie> {

        return repository.findByDirectorMovies(director)
    }

    fun
}