package com.kpanov.filmcatalog.service

import com.kpanov.filmcatalog.repository.DirectorRepository
import com.kpanov.filmcatalog.repository.entity.Director
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
class DirectorService(
    private val directorRepository: DirectorRepository
) {

    fun getDirectorsByName(directorName: String = "") : List<Director>
            = directorRepository.findDirectorsByFullNameContaining(directorName)

    fun getDirectorById(id : Long) : Director {
        return directorRepository.findDirectorById(id)
            ?: throw NoSuchElementException("Director with id $id not found.")
    }

    @Transactional
    fun createDirector(
        fullName: String,
        birthYear: Int
    ) {
        val director = Director(fullName = fullName, birthYear = birthYear)

        directorRepository.save(director)
    }
}