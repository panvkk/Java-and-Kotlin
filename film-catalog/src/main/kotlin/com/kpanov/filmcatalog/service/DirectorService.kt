package com.kpanov.filmcatalog.service

import com.kpanov.filmcatalog.repository.DirectorRepository
import com.kpanov.filmcatalog.repository.entity.DirectorEntity
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DirectorService(
    private val directorRepository: DirectorRepository
) {

    fun getDirectorsByName(directorName: String = "") : List<DirectorEntity>
            = directorRepository.findDirectorsByFullNameContaining(directorName)

    fun getDirectorById(id : Long) : DirectorEntity {
        return directorRepository.findDirectorById(id)
            ?: throw EntityNotFoundException("Director with id $id not found.")
    }

    @Transactional
    fun createDirector(
        fullName: String,
        birthYear: Int
    ) : DirectorEntity {
        val directorEntity = DirectorEntity(fullName = fullName, birthYear = birthYear)

        return directorRepository.save(directorEntity)
    }

    @Transactional
    fun deleteDirector(id: Long) {
        directorRepository.deleteDirectorById(id)
    }
}