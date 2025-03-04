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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AutocompleteViewModel : ViewModel() {
    private val _isSuggestionsVisible = MutableStateFlow(true) // Estado para controlar la visibilidad
    val isSuggestionsVisible: StateFlow<Boolean> = _isSuggestionsVisible

    // Función para ocultar las sugerencias
    fun hideSuggestions() {
        _isSuggestionsVisible.value = false
    }

    // Función para mostrar las sugerencias
    fun showSuggestions() {
        _isSuggestionsVisible.value = true
    }

    // Estado para las sugerencias y el texto de búsqueda
    val suggestions = mutableStateListOf<String>()

    // Estado para almacenar las coordenadas obtenidas
    private val _coordinates = mutableStateOf<GeocodeResponseToCordenates?>(null)

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

    fun fetchCoordinates(address: String, onSuccess: (latitude: Double, longitude: Double) -> Unit) {
        val request = AddressRequest(address)
        val call = RetrofitInstance.api.getCoordinates(request)

        call.enqueue(object : Callback<GeocodeResponseToCordenates> {
            override fun onResponse(
                call: Call<GeocodeResponseToCordenates>,
                response: Response<GeocodeResponseToCordenates>
            ) {
                if (response.isSuccessful) {
                    val coordinates = response.body()
                    coordinates?.let {
                        Log.d("Coordinates", "Lat: ${it.latitude}, Lng: ${it.longitude}")
                        // Llamar al callback con las coordenadas obtenidas
                        onSuccess(it.latitude, it.longitude)
                    }
                } else {
                    Log.e("Error", "Error en la respuesta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GeocodeResponseToCordenates>, t: Throwable) {
                Log.e("Error", "Fallo en la llamada: ${t.message}")
            }
        })
    }
}
