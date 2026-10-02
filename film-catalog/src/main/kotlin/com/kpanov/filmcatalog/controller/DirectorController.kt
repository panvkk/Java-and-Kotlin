package com.kpanov.filmcatalog.controller

import com.kpanov.filmcatalog.controller.dto.DirectorDto
import com.kpanov.filmcatalog.controller.dto.NewDirectorDto
import com.kpanov.filmcatalog.controller.mapper.toDto
import com.kpanov.filmcatalog.service.DirectorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class DirectorController(
    private val directorService: DirectorService
) {
    @GetMapping
    fun getDirectors() : List<DirectorDto> {
        return directorService.getDirectors().map { it.toDto() }
    }

    @PutMapping
    fun createDirector(
        @RequestBody newDirector: NewDirectorDto
    ) {
        directorService.createDirector(
            newDirector.fullName,
            newDirector.birthYear
        )
    }
}