package com.example.animebrowser

data class Anime(
    val id: Int,
    val title: String,
    val rating: Double,
    val imageUrl: String? = null,
    val year: Int? = null,
    val episodes: Int? = null,
    val type: String? = null,
    val synopsis: String? = null,
    val genres: String? = null
)