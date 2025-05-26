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
)

data class Review(
    val authorName: String? = null,
    val text: String? = null,
    val rating: Double? = null,
    val timeDescription: String? = null
)