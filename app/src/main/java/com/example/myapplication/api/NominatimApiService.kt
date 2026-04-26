package com.example.myapplication.api

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "jsonv2",
        @Query("limit") limit: Int = 1
    ): List<NominatimPlace>
}

data class NominatimPlace(
    val lat: String? = null,
    val lon: String? = null
)
