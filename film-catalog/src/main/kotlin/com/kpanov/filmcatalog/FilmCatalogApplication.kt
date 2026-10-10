package com.kpanov.filmcatalog

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class FilmCatalogApplication

fun main(vararg args: String) {
    SpringApplication.run(FilmCatalogApplication::class.java, *args)
}