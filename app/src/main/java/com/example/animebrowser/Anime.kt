package com.example.animebrowser

data class Anime(
    val id: Int,
    val title: String,
    val rating: Double,
    val imageUrl: String? = null  // За сад ће бити null, касније додајемо слике
)