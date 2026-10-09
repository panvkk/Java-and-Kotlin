package com.kpanov.filmcatalog.controller

import com.kpanov.filmcatalog.controller.dto.CreateDirectorRequestDto
import com.kpanov.filmcatalog.controller.dto.DirectorResponseDto
import com.kpanov.filmcatalog.controller.dto.UpdateDirectorRequestDto
import com.kpanov.filmcatalog.controller.mapper.toDto
import com.kpanov.filmcatalog.controller.mapper.toModel
import com.kpanov.filmcatalog.service.DirectorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class DirectorController(
    private val directorService: DirectorService
) {
    @GetMapping("/directors")
    fun getDirectors(
        @RequestParam("director_name", required = false) directorName: String
    ) : ResponseEntity<List<DirectorResponseDto>> {
        val directors = directorService.getDirectorsByName(directorName).map { it.toDto() }
        return ResponseEntity(directors, HttpStatus.OK)
    }

    @PostMapping("/directors")
    fun createDirector(
        @RequestBody newDirector: CreateDirectorRequestDto
    ) : ResponseEntity<DirectorResponseDto> {
        val savedDirector = directorService.createDirector(
            newDirector.fullName,
            newDirector.birthYear
        ).toDto()

        return ResponseEntity(savedDirector, HttpStatus.CREATED)
    }

    @DeleteMapping("/directors/{director_id}")
    fun deleteDirector(
        @PathVariable("director_id") directorId: Long
    ) : ResponseEntity<Unit> {
        directorService.deleteDirector(directorId)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PatchMapping("/directors/{director_id}")
    fun changeDirector(
        @PathVariable("director_id") directorId: Long,
        @RequestBody newDirector: UpdateDirectorRequestDto
    ) : ResponseEntity<DirectorResponseDto> {
        val savedDirector = directorService.updateDirector(directorId, newDirector.toModel())

        return ResponseEntity(savedDirector.toDto(), HttpStatus.OK)
    }
}