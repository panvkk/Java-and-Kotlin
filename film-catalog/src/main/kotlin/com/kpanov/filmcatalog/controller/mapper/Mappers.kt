package com.kpanov.filmcatalog.controller.mapper

import com.kpanov.filmcatalog.controller.dto.DirectorDto
import com.kpanov.filmcatalog.controller.dto.MovieDto
import com.kpanov.filmcatalog.repository.entity.Director
import com.kpanov.filmcatalog.repository.entity.Movie

fun Director.toDto() = DirectorDto(id = id, fullName = fullName, birthYear = birthYear)

fun Movie.toDto() = MovieDto(
    id = id,
    title = title,
    year = releaseYear,
    rating = rating,
    directorId = director.id,
    directorName = director.fullName
)