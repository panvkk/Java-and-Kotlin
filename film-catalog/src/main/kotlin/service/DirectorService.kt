package org.example.service

import org.example.entity.Director
import org.example.repository.DirectorRepository
import org.springframework.stereotype.Service

@Service
class DirectorService(
    private val directorRepository: DirectorRepository
) {
    fun getDirectors() : List<Director> = directorRepository.findAll()

    fun getDirectorsByName(directorName: String) : List<Director>
            = directorRepository.findDirectorsByFullName(directorName)
}