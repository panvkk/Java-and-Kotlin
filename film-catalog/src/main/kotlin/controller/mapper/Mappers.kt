package controller.mapper

import controller.dto.DirectorDto
import controller.dto.MovieDto
import org.example.entity.Director
import org.example.entity.Movie

fun Director.toDto() = DirectorDto(id = id, fullName = fullName, birthYear = birthYear)

fun Movie.toDto() = MovieDto(
    id = id,
    title = title,
    year = year,
    rating = rating,
    directorId = director.id,
    directorName = director.fullName
)