package controller

import controller.dto.MovieDto
import controller.dto.NewMovieDto
import controller.mapper.toDto
import org.example.service.MovieService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/")
class MovieController(
    private val movieService: MovieService
) {
    @GetMapping("/movies")
    fun getMoviesByTitle(
        @RequestParam(value = "title", required = false) title: String?
    ) : List<MovieDto> {
        return movieService.getMoviesByTitle(title).map { it.toDto() }
    }

    @PutMapping("/create-movie")
    fun createMovie(
        @RequestBody newMovie: NewMovieDto
    ) {
        movieService.createMovie(
            title = newMovie.title,
            year = newMovie.year,
            directorId = newMovie.directorId
        )
    }
}