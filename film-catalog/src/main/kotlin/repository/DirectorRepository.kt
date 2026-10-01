package org.example.repository

import org.example.entity.Director
import org.springframework.data.jpa.repository.JpaRepository

interface DirectorRepository : JpaRepository<Director, Long> {

    fun findDirectorsByFullName(directorName: String) : List<Director>

}