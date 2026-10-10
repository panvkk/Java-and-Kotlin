package com.kpanov.filmcatalog.repository

import com.kpanov.filmcatalog.repository.entity.DirectorEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DirectorRepository : JpaRepository<DirectorEntity, Long> {

    fun findDirectorsByFullNameContaining(directorName: String) : List<DirectorEntity>

    fun findDirectorById(id: Long) : DirectorEntity?

    fun deleteDirectorById(id: Long)
}