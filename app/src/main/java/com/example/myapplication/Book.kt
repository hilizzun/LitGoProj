package com.example.myapplication

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val genre: String = "",
    val status: String = "",
    val progress: Int = 0,
    val coverRes: Int = 0,
    val coverUri: String? = null,
    val year: Int = 0,
    val description: String = "",
    val review: String = "",
    val rating: Float = 0f
)
