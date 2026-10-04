package com.kpanov.filmcatalog.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateDirectorRequest(
    @JsonProperty("full_name")
    val fullName: String,
    @JsonProperty("birth_year")
    val birthYear: Int
)
