package com.kpanov.filmcatalog.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NewMovieDto(
    val title: String,
    val year: Int,
    @JsonProperty("director_id")
    val directorId: Long
)