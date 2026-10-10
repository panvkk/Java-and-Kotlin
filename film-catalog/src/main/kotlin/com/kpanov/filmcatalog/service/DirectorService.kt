package com.kpanov.filmcatalog.service

import com.kpanov.filmcatalog.repository.DirectorRepository
import com.kpanov.filmcatalog.repository.entity.DirectorEntity
import com.kpanov.filmcatalog.service.model.UpdateDirectorModel
import com.kpanov.filmcatalog.util.DIRECTOR_NOT_FOUND_BY_ID_MESSAGE
import com.kpanov.filmcatalog.util.INVALID_BIRTH_YEAR_MESSAGE
import com.kpanov.filmcatalog.util.MIN_BIRTH_YEAR
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DirectorService(
    private val directorRepository: DirectorRepository
) {

    fun getDirectorsByName(directorName: String?) : List<DirectorEntity>
            = directorRepository.findDirectorsByFullNameContaining(directorName ?: "")

    fun getDirectorById(id : Long) : DirectorEntity {
        return directorRepository.findDirectorById(id)
            ?: throw EntityNotFoundException(DIRECTOR_NOT_FOUND_BY_ID_MESSAGE.format(id))
    }

    /** Конкретного практического смысла в этой аннотации нет, так как ни в одном методе сервисов у
     * меня нет 2-х и более вызовов изменяющих БД функций. Однако придерживаясь определённого код-стиля,
     * я решил всё же добавить аннотации на все методы, которые хоть что-то изменяют (мало ли, в метод
     * добавиться ещё одна изменяющая функция).
     */
    @Transactional
    fun createDirector(
        fullName: String,
        birthYear: Int
    ) : DirectorEntity {
        validateBirthYear(birthYear)

        val directorEntity = DirectorEntity(fullName = fullName, birthYear = birthYear)

        return directorRepository.save(directorEntity)
    }

    @Transactional
    fun deleteDirector(id: Long) {
        directorRepository.deleteDirectorById(id)
    }

    @Transactional
    fun updateDirector(directorId: Long, newDirector: UpdateDirectorModel) : DirectorEntity {
        validateBirthYear(newDirector.birthYear)

        val currentDirectorEntity = directorRepository.findDirectorById(directorId)
            ?: throw EntityNotFoundException(DIRECTOR_NOT_FOUND_BY_ID_MESSAGE.format(directorId))

        val newDirectorEntity = currentDirectorEntity.copy(
            fullName = newDirector.fullName,
            birthYear = newDirector.birthYear
        )

        return directorRepository.save(newDirectorEntity)
    }

    private fun validateBirthYear(birthYear: Int) {
        require(birthYear > MIN_BIRTH_YEAR) {
            INVALID_BIRTH_YEAR_MESSAGE.format(birthYear)
        }
    }
}