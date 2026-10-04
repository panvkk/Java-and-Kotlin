package com.kpanov.filmcatalog.controller

import com.kpanov.filmcatalog.controller.dto.DirectorResponseDto
import com.kpanov.filmcatalog.controller.dto.CreateDirectorRequest
import com.kpanov.filmcatalog.controller.mapper.toDto
import com.kpanov.filmcatalog.service.DirectorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
    ) : List<DirectorResponseDto> {
        return directorService.getDirectorsByName(directorName).map { it.toDto() }
    }

    @PostMapping("/create-director")
    fun createDirector(
        @RequestBody newDirector: CreateDirectorRequest
    ) {
        directorService.createDirector(
            newDirector.fullName,
            newDirector.birthYear
        )
    }
}