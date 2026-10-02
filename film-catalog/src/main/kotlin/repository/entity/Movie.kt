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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = false) // пока не совсем понимаю когда надо ставить nullable, а когда нет
    val director: Director
)