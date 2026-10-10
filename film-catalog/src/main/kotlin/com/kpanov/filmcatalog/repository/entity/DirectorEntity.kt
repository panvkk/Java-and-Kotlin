package com.kpanov.filmcatalog.repository.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "directors_table")
data class DirectorEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "full_name")
    val fullName: String,
    @Column(name = "birth_year")
    val birthYear: Int,
    @OneToMany(mappedBy = "director", cascade = [CascadeType.ALL], orphanRemoval = true)
    val movieEntities: List<MovieEntity> = emptyList()
)