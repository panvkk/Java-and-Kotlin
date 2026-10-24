package com.kpanov.filmcatalog.controller

import com.kpanov.filmcatalog.controller.dto.MovieResponseDto
import com.kpanov.filmcatalog.controller.dto.CreateMovieRequestDto
import com.kpanov.filmcatalog.controller.dto.UpdateMovieRequestDto
import com.kpanov.filmcatalog.controller.mapper.toDto
import com.kpanov.filmcatalog.controller.mapper.toModel
import com.kpanov.filmcatalog.service.MovieService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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

    @GetMapping("/movies/{director_id}")
    fun getDirectorMovies(
        @PathVariable("director_id") directorId: Long)
    : ResponseEntity<List<MovieResponseDto>> {
        val movies = movieService.getDirectorMovies(directorId).map { it.toDto() }
        return ResponseEntity(movies, HttpStatus.OK)
    }

    @DeleteMapping("/movies/{movie_id}")
    fun deleteMovie(
        @PathVariable("movie_id") movieId: Long
    ) : ResponseEntity<Unit> {
        movieService.deleteMovie(movieId)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/movies")
    fun createMovie(
        @RequestBody newMovie: CreateMovieRequestDto
    ) : ResponseEntity<MovieResponseDto> {
        val savedMovie = movieService.createMovie(
            title = newMovie.title,
            year = newMovie.year,
            directorId = newMovie.directorId
        ).toDto()

        return ResponseEntity(savedMovie, HttpStatus.CREATED)
    }

    @PatchMapping("/movies/{movie_id}")
    fun updateMovie(
        @PathVariable("movie_id") movieId: Long,
        @RequestBody newMovie: UpdateMovieRequestDto
    ) : ResponseEntity<MovieResponseDto> {
        val savedMovie = movieService.updateMovie(movieId, newMovie.toModel())

        return ResponseEntity(savedMovie.toDto(), HttpStatus.OK)
    }
}