package com.example.myapplication.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OverpassApiService {
    @FormUrlEncoded
    @POST("api/interpreter")
    suspend fun query(@Field("data") query: String): OverpassResponse
}

data class OverpassResponse(
    val elements: List<OverpassElement>? = null
)

data class OverpassElement(
    val lat: Double? = null,
    val lon: Double? = null,
    val center: OverpassCenter? = null,
    val tags: OverpassTags? = null
)

data class OverpassCenter(
    val lat: Double? = null,
    val lon: Double? = null
)

data class OverpassTags(
    @SerializedName("name")
    val name: String? = null
)
