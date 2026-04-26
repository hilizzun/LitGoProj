package com.example.myapplication.api

data class BookDraft(
    val title: String = "",
    val author: String = "",
    val year: String = "",
    val description: String = "",
    val pages: String = "",
    val genre: String = ""
)

data class BookstorePoint(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

class BookCatalogRepository(
    private val googleBooksApiService: GoogleBooksApiService = ApiClient.googleBooksApi,
    private val yandexPlacesApiService: YandexPlacesApiService = ApiClient.yandexPlacesApi
) {

    suspend fun searchBookByTitle(title: String): BookDraft? {
        val response = googleBooksApiService.searchVolumes(query = "intitle:$title")
        val volumeInfo = response.items?.firstOrNull()?.volumeInfo ?: return null

        val year = volumeInfo.publishedDate
            ?.take(4)
            ?.filter { it.isDigit() }
            .orEmpty()

        val genreFromApi = volumeInfo.categories?.firstOrNull().orEmpty()
        val mappedGenre = when {
            genreFromApi.contains("fiction", ignoreCase = true) -> "Фантастика"
            genreFromApi.contains("detective", ignoreCase = true) -> "Детектив"
            genreFromApi.contains("poetry", ignoreCase = true) -> "Поэзия"
            genreFromApi.isBlank() -> ""
            else -> "Научная литература"
        }

        return BookDraft(
            title = volumeInfo.title.orEmpty(),
            author = volumeInfo.authors?.joinToString(", ").orEmpty(),
            year = year,
            description = volumeInfo.description.orEmpty(),
            pages = volumeInfo.pageCount?.toString().orEmpty(),
            genre = mappedGenre
        )
    }

    suspend fun findBookstoresByCity(city: String, apiKey: String): List<BookstorePoint> {
        if (apiKey.isBlank()) return emptyList()

        val geo = yandexPlacesApiService.search(
            apiKey = apiKey,
            text = city,
            type = "geo",
            results = 1
        )
        val cityCoords = geo.features
            ?.firstOrNull()
            ?.geometry
            ?.coordinates
            ?.takeIf { it.size >= 2 }
            ?: return emptyList()

        val ll = "${cityCoords[0]},${cityCoords[1]}"
        val bookstores = yandexPlacesApiService.search(
            apiKey = apiKey,
            text = "книжный магазин",
            type = "biz",
            ll = ll,
            spn = "0.4,0.4",
            results = 30
        )

        return bookstores.features.orEmpty().mapNotNull { feature ->
            val coords = feature.geometry?.coordinates
            if (coords == null || coords.size < 2) return@mapNotNull null

            val name = feature.properties?.companyMetaData?.name
                ?: feature.properties?.name
                ?: "Книжный магазин"

            BookstorePoint(
                name = name,
                latitude = coords[1],
                longitude = coords[0]
            )
        }
    }
}
