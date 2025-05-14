package com.example.islamicnames.model

data class Name(
    val id: Int,
    val name: String,
    val meaning: String,
    val arabicName: String,
    val gender: Gender,
    var isFavorite: Boolean = false
)

enum class Gender {
    BOY, GIRL
}
