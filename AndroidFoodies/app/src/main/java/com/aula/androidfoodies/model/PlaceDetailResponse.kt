package com.aula.androidfoodies.model

data class PlaceDetailResponse(
    val name: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val website: String? = null,
    val rating: Double? = null,
    val priceLevel: Int? = null,
    val openingHours: String? = null,
    val photos: List<String>? = null,
    val reviews: List<Review>? = null
) {
    val priceLevelDescription: String
        get() = when (priceLevel) {
            0 -> "Gratis"
            1 -> "Barato"
            2 -> "Moderado"
            3 -> "Caro"
            4 -> "Muy caro"
            else -> "Desconocido"
        }
}


data class Review(
    val authorName: String? = null,
    val text: String? = null,
    val rating: Double? = null,
    val timeDescription: String? = null
)

data class Restaurant(
    val name: String,
    val lat: Double,
    val lon: Double
)