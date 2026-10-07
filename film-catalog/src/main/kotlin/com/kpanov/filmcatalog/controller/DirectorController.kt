package com.kpanov.filmcatalog.controller

import com.kpanov.filmcatalog.controller.dto.DirectorResponseDto
import com.kpanov.filmcatalog.controller.dto.CreateDirectorRequestDto
import com.kpanov.filmcatalog.controller.mapper.toDto
import com.kpanov.filmcatalog.service.DirectorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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

    @PostMapping("/create-director")
    fun createDirector(
        @RequestBody newDirector: CreateDirectorRequestDto
    ) : ResponseEntity<DirectorResponseDto> {
        val savedDirector = directorService.createDirector(
            newDirector.fullName,
            newDirector.birthYear
        ).toDto()

        return ResponseEntity(savedDirector, HttpStatus.CREATED)
    }

    @DeleteMapping("/delete-director")
    fun deleteDirector(
        @RequestParam("director_id") directorId: Long
    ) {
        directorService.deleteDirector(directorId)
    }

    @PatchMapping("/directors/{director_id}/change")
    fun changeDirector(
        @PathVariable("director_id") directorId: Long,
    )
}