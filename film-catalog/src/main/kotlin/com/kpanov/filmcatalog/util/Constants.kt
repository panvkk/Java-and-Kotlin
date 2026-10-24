package com.kpanov.filmcatalog.util

const val MIN_BIRTH_YEAR = 0
const val MIN_RELEASE_YEAR = 0

const val DIRECTOR_NOT_FOUND_BY_ID_MESSAGE = "Director with id %d not found."
const val MOVIE_NOT_FOUND_BY_ID_MESSAGE = "Movie with id %d not found."
const val INVALID_RATING_MESSAGE = "Invalid rating: %f. Rating must be between 0 and 10."
const val INVALID_BIRTH_YEAR_MESSAGE = "Invalid birth year: %d. Birth year must be more than $MIN_BIRTH_YEAR"
const val INVALID_RELEASE_YEAR_MESSAGE = "Invalid release year: %d. Release year must be more than $MIN_RELEASE_YEAR"

const val MAX_RATING = 10f
const val MIN_RATING = 0f

const val RATING_GENERATION_FACTOR = 10