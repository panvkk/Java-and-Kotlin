package com.kpanov.filmcatalog.controller.mapper

import com.kpanov.filmcatalog.controller.dto.DirectorResponseDto
import com.kpanov.filmcatalog.controller.dto.MovieResponseDto
import com.kpanov.filmcatalog.controller.dto.UpdateDirectorRequestDto
import com.kpanov.filmcatalog.controller.dto.UpdateMovieRequestDto
import com.kpanov.filmcatalog.repository.entity.DirectorEntity
import com.kpanov.filmcatalog.repository.entity.MovieEntity
import com.kpanov.filmcatalog.service.dto.UpdateDirectorModel
import com.kpanov.filmcatalog.service.dto.UpdateMovieModel

fun DirectorEntity.toDto() = DirectorResponseDto(id = id, fullName = fullName, birthYear = birthYear)

fun MovieEntity.toDto() = MovieResponseDto(
    id = id,
    title = title,
    year = releaseYear,
    rating = rating,
    directorId = director.id,
    directorName = director.fullName
)

fun UpdateDirectorRequestDto.toModel() = UpdateDirectorModel(
    fullName = fullName,
    birthYear = birthYear
)

fun UpdateMovieRequestDto.toModel() = UpdateMovieModel(
    title = title,
    year = year,
    rating = rating
)