package com.example.myapplication.api

import retrofit2.http.GET
import retrofit2.http.Query

interface YandexPlacesApiService {

    @GET(".")
    suspend fun search(
        @Query("apikey") apiKey: String,
        @Query("text") text: String,
        @Query("lang") lang: String = "ru_RU",
        @Query("type") type: String? = null,
        @Query("ll") ll: String? = null,
        @Query("spn") spn: String? = null,
        @Query("results") results: Int = 10
    ): YandexPlacesResponse
}
