package com.aula.androidfoodies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.aula.androidfoodies.model.AutocompleteResponse
import com.aula.androidfoodies.retrofit.RetrofitInstance
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class AutocompleteViewModel : ViewModel() {
    val suggestions = mutableStateListOf<String>()
    fun fetchAutocompleteSuggestions(input: String) {
        // Verifica que el input no esté vacío
        if (input.isBlank()) {
            suggestions.clear()
            return
        }

        viewModelScope.launch {
            try {
                // Cuerpo de la solicitud con el input del usuario
                val requestBody = mapOf("input" to input, "sessiontoken" to "token_unico")
                val response = RetrofitInstance.api.getAutocomplete(requestBody)

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        // Parsear la respuesta JSON
                        val gson = Gson()
                        val type = object : TypeToken<AutocompleteResponse>() {}.type
                        val autocompleteResponse: AutocompleteResponse = gson.fromJson(responseBody, type)

                        // Actualizar la lista de sugerencias
                        suggestions.clear()
                        autocompleteResponse.predictions?.let { predictionList ->
                            val suggestionTexts = predictionList.map { it.description }
                            suggestions.addAll(suggestionTexts)
                        }
                    }
                } else {
                    Log.e("Autocomplete", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Autocomplete", "Error en la petición: ${e.message}")
            }
        }
    }
}
