package com.aula.androidfoodies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.aula.androidfoodies.model.AddressRequest
import com.aula.androidfoodies.model.GeocodeResponseToCordenates
import com.aula.androidfoodies.retrofit.RetrofitInstance
import java.util.UUID

class AutocompleteViewModel : ViewModel() {

    // Estado para las sugerencias y el texto de búsqueda
    val suggestions = mutableStateListOf<String>()
    val searchQuery = mutableStateOf("")
    val address = mutableStateOf("")

    // Estado para almacenar las coordenadas obtenidas
    private val _coordinates = mutableStateOf<GeocodeResponseToCordenates?>(null)
    val coordinates: State<GeocodeResponseToCordenates?> = _coordinates

    // Estado para almacenar los restaurantes obtenidos según las coordenadas
    private val _restaurants = mutableStateOf<List<Map<String, String>>>(emptyList())
    val restaurants: State<List<Map<String, String>>> = _restaurants

    fun fetchAutocompleteSuggestions(input: String) {
        if (input.isBlank()) {
            suggestions.clear()
            return
        }
        viewModelScope.launch {
            try {
                // Usamos el sessionToken persistente en la llamada
                val response = RetrofitInstance.api.getAutocomplete(
                    input = input
                )
                if (response.isSuccessful) {
                    response.body()?.let { autocompleteResponse ->
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

    fun fetchNearbyRestaurants(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.fetchNearbyRestaurants(latitude, longitude)
                if (response.isSuccessful) {
                    _restaurants.value = response.body() ?: emptyList()
                } else {
                    Log.e("Autocomplete", "Error en la respuesta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Autocomplete", "Error en la llamada: ${e.message}")
            }
        }
    }

    fun adressToCordenates(adress: String, onSuccess: (GeocodeResponseToCordenates?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.parseAdress(AddressRequest(adress))
                if (response.isSuccessful) {
                    response.body()?.let { geoResponse ->
                        _coordinates.value = geoResponse
                        onSuccess(geoResponse)
                    } ?: throw Exception("El cuerpo de la respuesta es nulo")
                } else {
                    throw Exception("Error en la respuesta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Autocomplete", "Error en la llamada: ${e.message}")
                throw e
            }
        }
    }
}
