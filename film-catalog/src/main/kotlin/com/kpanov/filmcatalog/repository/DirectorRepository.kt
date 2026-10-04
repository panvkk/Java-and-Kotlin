package com.kpanov.filmcatalog.repository

import com.kpanov.filmcatalog.repository.entity.Director
import org.springframework.data.jpa.repository.JpaRepository

interface DirectorRepository : JpaRepository<Director, Long> {

    fun findDirectorsByFullNameContaining(directorName: String) : List<Director>

    fun findDirectorById(id: Long) : Director?
}