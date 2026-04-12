package com.example.myapplication

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val genre: String,
    val status: String,
    val progress: Int,
    val coverRes: Int
)