package com.kpanov.filmcatalog.controller.dto

data class UpdateMovieRequestDto(
    val title: String,
    val year: Int,
    val rating: Float
)
