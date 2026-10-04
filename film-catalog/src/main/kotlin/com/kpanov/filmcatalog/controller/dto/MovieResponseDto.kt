package com.kpanov.filmcatalog.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MovieResponseDto(
    val id: Long,
    val title: String,
    val year: Int,
    val rating: Float,
    @JsonProperty("director_id")
    val directorId: Long,
    @JsonProperty("director_name")
    val directorName: String
)
