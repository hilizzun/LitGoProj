package com.example.myapplication.api

data class BookDraft(
    val title: String = "",
    val author: String = "",
    val year: String = "",
    val description: String = "",
    val pages: String = "",
    val genre: String = "",
    val coverUrl: String = ""
)

data class BookstorePoint(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

class BookCatalogRepository(
    private val googleBooksApiService: GoogleBooksApiService = ApiClient.googleBooksApi,
    private val nominatimApiService: NominatimApiService = ApiClient.nominatimApi,
    private val overpassApiService: OverpassApiService = ApiClient.overpassApi
) {
    suspend fun searchBookByTitle(title: String): BookDraft? {
        val response = googleBooksApiService.searchVolumes(query = "intitle:$title")
        val volumeInfo = response.items?.firstOrNull()?.volumeInfo ?: return null

        val year = volumeInfo.publishedDate
            ?.take(4)
            ?.filter { it.isDigit() }
            .orEmpty()

        val genre = mapCategoryToGenre(volumeInfo.categories?.firstOrNull().orEmpty())

        return BookDraft(
            title = volumeInfo.title.orEmpty(),
            author = volumeInfo.authors?.joinToString(", ").orEmpty(),
            year = year,
            description = volumeInfo.description.orEmpty(),
            pages = volumeInfo.pageCount?.toString().orEmpty(),
            genre = genre,
            coverUrl = volumeInfo.imageLinks?.thumbnail
                ?.replace("http://", "https://")
                .orEmpty()
        )
    }

    suspend fun findBookstoresByCity(city: String): List<BookstorePoint> {
        val place = nominatimApiService.search(city).firstOrNull() ?: return emptyList()
        val centerLat = place.lat?.toDoubleOrNull() ?: return emptyList()
        val centerLon = place.lon?.toDoubleOrNull() ?: return emptyList()

        val overpassQuery = """
            [out:json][timeout:25];
            (
              node["shop"="books"](around:20000,$centerLat,$centerLon);
              way["shop"="books"](around:20000,$centerLat,$centerLon);
              relation["shop"="books"](around:20000,$centerLat,$centerLon);
            );
            out center;
        """.trimIndent()

        val response = overpassApiService.query(overpassQuery)

        return response.elements.orEmpty().mapNotNull { element ->
            val lat = element.lat ?: element.center?.lat ?: return@mapNotNull null
            val lon = element.lon ?: element.center?.lon ?: return@mapNotNull null
            val name = element.tags?.name ?: "Книжный магазин"
            BookstorePoint(name = name, latitude = lat, longitude = lon)
        }
    }

    private fun mapCategoryToGenre(category: String): String {
        if (category.isBlank()) return ""
        val lower = category.lowercase()
        return when {
            "detective" in lower || "crime" in lower -> "Детектив"
            "fantasy" in lower || "fiction" in lower || "sci-fi" in lower -> "Фантастика"
            "poetry" in lower -> "Поэзия"
            "romance" in lower || "novel" in lower || "classic" in lower -> "Роман"
            else -> "Научная литература"
        }
    }
}
