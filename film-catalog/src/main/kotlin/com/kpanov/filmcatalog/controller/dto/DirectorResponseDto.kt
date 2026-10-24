package com.kpanov.filmcatalog.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DirectorResponseDto(
    val id: Long = 0,
    @JsonProperty("full_name")
    val fullName: String,
    @JsonProperty("birth_year")
    val birthYear: Int
)
