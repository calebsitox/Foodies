package com.aula.androidfoodies.model

data class GeocodeRequest(val latitude: Double, val longitude: Double)

data class GeocodeResponseToCordenates(
    val latitude: Double,
    val longitude: Double
)

data class AddressRequest(
    val adress: String
)