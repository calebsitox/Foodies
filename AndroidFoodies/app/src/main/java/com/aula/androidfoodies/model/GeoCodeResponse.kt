package com.aula.androidfoodies.model

import com.google.android.libraries.places.api.model.Place

data class GeoCodeResponse(
    val status: String,
    val results: List<Place>?
)