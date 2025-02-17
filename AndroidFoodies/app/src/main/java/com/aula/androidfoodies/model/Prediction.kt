package com.aula.androidfoodies.model

data class Prediction(
    val description: String
    // Otros campos según la respuesta de la API
)


data class AutocompleteResponse(
    val predictions: List<Prediction>?
    // Otros campos según la respuesta de la API
)