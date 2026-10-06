package com.kpanov.filmcatalog.controller

import com.kpanov.filmcatalog.controller.dto.MovieResponseDto
import com.kpanov.filmcatalog.controller.dto.CreateMovieRequest
import com.kpanov.filmcatalog.controller.mapper.toDto
import com.kpanov.filmcatalog.service.MovieService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class MovieController(
    private val movieService: MovieService
) {
    @GetMapping("/movies")
    fun getMovies(
        @RequestParam("title", required = false) title: String?,
        @RequestParam("minimal_rating", required = false) minimalRating: Float?
    ) : ResponseEntity<List<MovieResponseDto>> {
        val movies = movieService.getMovies(title, minimalRating).map { it.toDto() }
        return ResponseEntity(movies, HttpStatus.OK)
    }

    @GetMapping("/directors/{director_id}/movies")
    fun getDirectorMovies(
        @PathVariable("director_id") directorId: Long)
    : ResponseEntity<List<MovieResponseDto>> {
        val movies = movieService.getDirectorMovies(directorId).map { it.toDto() }
        return ResponseEntity(movies, HttpStatus.OK)
    }

    @PostMapping("/create-movie")
    fun createMovie(
        @RequestBody newMovie: CreateMovieRequest
    ) : ResponseEntity<MovieResponseDto> {
        val savedMovie = movieService.createMovie(
            title = newMovie.title,
            year = newMovie.year,
            directorId = newMovie.directorId
        ).toDto()

        return ResponseEntity(savedMovie, HttpStatus.CREATED)
    }
}