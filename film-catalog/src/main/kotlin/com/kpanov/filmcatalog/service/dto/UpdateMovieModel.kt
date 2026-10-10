package com.kpanov.filmcatalog.service.dto

data class UpdateMovieModel(
    val title: String,
    val year: Int,
    val rating: Float
)
