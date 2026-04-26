package com.example.myapplication.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {

    @GET("volumes")
    suspend fun searchVolumes(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 5,
        @Query("langRestrict") langRestrict: String = "ru"
    ): GoogleBooksResponse
}
