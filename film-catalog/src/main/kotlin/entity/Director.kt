package org.example.entity

import jakarta.persistence.*

@Entity
@Table(name = "directors_table")
data class Director(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "full_name")
    val fullName: String,
    @Column(name = "birth_year")
    val birthYear: Int,
    @OneToMany(mappedBy = "director", cascade = [CascadeType.ALL], orphanRemoval = true)
    val movies: List<Movie> = emptyList()
)