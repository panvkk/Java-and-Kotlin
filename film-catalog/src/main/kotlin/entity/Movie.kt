package org.example.entity

import jakarta.persistence.*

@Entity
@Table(name = "movies_table")
data class Movie (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    val year: Int,
    val rating: Float,
    @ManyToOne
    @JoinColum()
    val directorId: Long,
    @Column("director_name")
    val directorName: String
)