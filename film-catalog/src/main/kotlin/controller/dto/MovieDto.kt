package controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MovieDto(
    val id: Long,
    val title: String,
    val year: Int,
    val rating: Float,
    @JsonProperty("director_id")
    val directorId: Long,
    @JsonProperty("director_name")
    val directorName: String
)
