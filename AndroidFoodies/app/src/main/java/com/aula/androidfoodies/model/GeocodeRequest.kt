package com.aula.androidfoodies.model

data class GeocodeRequest(val latitude: Double, val longitude: Double)

data class GeocodeResponse(val address: String)

