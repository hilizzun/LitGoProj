package com.example.myapplication.api

data class GoogleBooksResponse(
    val items: List<GoogleBookItem>? = null
)

data class GoogleBookItem(
    val volumeInfo: GoogleVolumeInfo? = null
)

data class GoogleVolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val pageCount: Int? = null,
    val categories: List<String>? = null,
    val imageLinks: GoogleImageLinks? = null
)

data class GoogleImageLinks(
    val thumbnail: String? = null
)
