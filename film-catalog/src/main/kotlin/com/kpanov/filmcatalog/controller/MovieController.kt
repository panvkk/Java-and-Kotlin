package com.kpanov.filmcatalog.controller

import com.kpanov.filmcatalog.controller.dto.MovieResponseDto
import com.kpanov.filmcatalog.controller.dto.CreateMovieRequest
import com.kpanov.filmcatalog.controller.mapper.toDto
import com.kpanov.filmcatalog.service.MovieService
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
    ) : List<MovieResponseDto> {
        return movieService.getMovies(title, minimalRating).map { it.toDto() }
    }

    @PostMapping("/create-movie")
    fun createMovie(
        @RequestBody newMovie: CreateMovieRequest
    ) {
        movieService.createMovie(
            title = newMovie.title,
            year = newMovie.year,
            directorId = newMovie.directorId
        )
    }
}