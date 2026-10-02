package org.example.service

import org.example.entity.Director
import org.example.repository.DirectorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
class DirectorService(
    private val directorRepository: DirectorRepository
) {
    fun getDirectors() : List<Director> = directorRepository.findAll()

    fun getDirectorsByName(directorName: String) : List<Director>
            = directorRepository.findDirectorsByFullName(directorName)

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