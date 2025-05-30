package com.aula.androidfoodies.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.aula.androidfoodies.model.AddressRequest
import com.aula.androidfoodies.model.GeocodeRequest
import com.aula.androidfoodies.model.GeocodeResponse
import com.aula.androidfoodies.model.GeocodeResponseToCordenates
import com.aula.androidfoodies.model.PlaceDetailResponse
import com.aula.androidfoodies.model.RestaurantRequest
import com.aula.androidfoodies.retrofit.RetrofitInstance
import com.aula.androidfoodies.retrofit.RetrofitInstance.api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutocompleteViewModel : ViewModel() {

    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location: StateFlow<Pair<Double, Double>?> = _location.asStateFlow()

    fun updateLocation(latitude: Double, longitude: Double) {
        _location.value = Pair(latitude, longitude)
    }

    // Estado para las sugerencias y el texto de búsqueda
    val suggestions = mutableStateListOf<String>()

    // Estado para almacenar las coordenadas obtenidas
    private val _coordinates = mutableStateOf<GeocodeResponseToCordenates?>(null)
    val coordinates: State<GeocodeResponseToCordenates?> = _coordinates

    // Estado para almacenar los restaurantes obtenidos según las coordenadas
    private val _restaurants = mutableStateOf<List<Map<String, String>>>(emptyList())
    val restaurants: State<List<Map<String, String>>> = _restaurants

    private val _likedRestaurants = mutableStateOf<List<Map<String, String>>>(emptyList())
    val likedRestaurants: State<List<Map<String, String>>> = _likedRestaurants


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

    fun fetchNearbyRestaurants(latitude: Double, longitude: Double, token: String) {
        viewModelScope.launch {
            try {

                val geocodeRequest = GeocodeRequest(latitude, longitude)
                val response = RetrofitInstance.api.fetchNearbyRestaurants(geocodeRequest, token)
                if (response.isSuccessful) {
                    _restaurants.value = response.body() ?: emptyList()
                } else {
                    Log.e(
                        "Autocomplete",
                        "Error en la respuesta: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("Autocomplete", "Error en la llamada: ${e.message}")
            }
        }
    }

    fun fetchCoordinates(
        address: String,
        onSuccess: (latitude: Double, longitude: Double) -> Unit
    ) {
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

    fun sendCoordinatesToBackend(lat: Double, lon: Double, token: String) {
        val request = GeocodeRequest(lat, lon)

        Log.d("API", "Sending coordinates: Latitude = $lat, Longitude = $lon")

        api.sendCoordinates(request, token).enqueue(object : Callback<GeocodeResponse> {
            override fun onResponse(
                call: Call<GeocodeResponse>,
                response: Response<GeocodeResponse>
            ) {
                if (response.isSuccessful) {
                    val address = response.body()?.address
                    println("Address received: $address")
                } else {
                    println("Server response error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                println("Failed to connect to the server: ${t.message}")
            }
        })
    }

    fun fetchPhotoUrl(photoReference: String, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = RetrofitInstance.api.getUrlPhoto(photoReference, token)
                println("URL de la foto: $url")
            } catch (e: Exception) {
                println("Error al obtener la URL: ${e.message}")
            }
        }
    }

    fun likeRestaurant(token: String, request: RestaurantRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                RetrofitInstance.api.likeRestaurant(token, request)
            } catch (e: Exception) {
                Log.e("LikeRestaurantError", "Error liking restaurant", e)
            }
        }
    }

    fun likedRestaurant(token: String, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.likedRestaurants(token, username)
                if (response.isSuccessful) {
                    _restaurants.value =
                        (response.body() ?: emptyList()) as List<Map<String, String>>
                    _likedRestaurants.value = response.body() ?: emptyList()
                } else {
                    Log.e(
                        "Autocomplete",
                        "Error en la respuesta: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("LikeRestaurantError", "Error liking restaurant", e)
            }
        }
    }

    fun unlikeRestaurant(token: String, request: RestaurantRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                RetrofitInstance.api.unlikeRestaurant(token, request)
            } catch (e: Exception) {
                Log.e("LikeRestaurantError", "Error liking restaurant", e)
            }
        }
    }



    suspend fun fetchPlaceDetails(token: String, request: GeocodeRequest): PlaceDetailResponse? {
        return try {
            val response = RetrofitInstance.api.resturantDetails(token, request)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("PlaceDetails", "Error en la respuesta: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("PlaceDetails", "Error: ${e.message}", e)
            null
        }
    }

    // Forma CORRECTA de construir la URL con parámetros
    fun buildPhotoUrl(photoRef: String): String {
        return Uri.parse("https://maps.googleapis.com/maps/api/place/photo")
            .buildUpon()
            .appendQueryParameter("maxwidth", "400")
            .appendQueryParameter("photoreference", photoRef) // No necesitas URLEncoder aquí
            .appendQueryParameter(
                "key",
                "AIzaSyCNSEbqAUraUirf4YqRBbdxflyysTWWx6c"
            ) // Tu API key
            .build()
            .toString()
    }


}


