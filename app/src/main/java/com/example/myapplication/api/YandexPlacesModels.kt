package com.example.myapplication.api

import com.google.gson.annotations.SerializedName

data class YandexPlacesResponse(
    val features: List<YandexFeature>? = null
)

data class YandexFeature(
    val geometry: YandexGeometry? = null,
    val properties: YandexProperties? = null
)

data class YandexGeometry(
    val coordinates: List<Double>? = null
)

data class YandexProperties(
    val name: String? = null,
    @SerializedName("description")
    val descriptionText: String? = null,
    @SerializedName("CompanyMetaData")
    val companyMetaData: YandexCompanyMetaData? = null
)

data class YandexCompanyMetaData(
    val name: String? = null,
    val address: String? = null
)
