package controller

import org.example.entity.Movie
import org.example.repository.MovieRepository
import org.example.service.MovieService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/catalog/")
class MovieController(
    private val movieService: MovieService
) {
    @GetMapping("/movies")
    fun getMoviesByTitle(
        @RequestParam(value = "title", required = false) title: String?
    ) : List<Movie> {
        return movieService.getMoviesByTitle(title)
    }


}