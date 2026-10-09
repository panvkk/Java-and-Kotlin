package com.kpanov.filmcatalog.repository

import com.kpanov.filmcatalog.repository.entity.DirectorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface DirectorRepository : JpaRepository<DirectorEntity, Long> {

    fun findDirectorsByFullNameContaining(directorName: String) : List<DirectorEntity>

    fun findDirectorById(id: Long) : DirectorEntity?

    fun deleteDirectorById(id: Long)

//    @Modifying
//    @Query("UPDATE DirectorEntity d " +
//                "SET d.fullName = :newFullName, d.birthYear = :newBirthYear " +
//                "WHERE d.id = :id")
//    fun updateDirector(id: Long, newFullName: String, newBirthYear: Int) // не уверен, насколько это оправдано
    // у меня идиоматичность кода головного мозга
}