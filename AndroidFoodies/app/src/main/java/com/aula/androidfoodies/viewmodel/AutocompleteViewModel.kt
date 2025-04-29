package com.aula.androidfoodies.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.aula.androidfoodies.model.AddressRequest
import com.aula.androidfoodies.model.GeocodeRequest
import com.aula.androidfoodies.model.GeocodeResponse
import com.aula.androidfoodies.model.GeocodeResponseToCordenates
import com.aula.androidfoodies.model.RestaurantRequest
import com.aula.androidfoodies.retrofit.RetrofitInstance
import com.aula.androidfoodies.retrofit.RetrofitInstance.api
import com.aula.androidfoodies.service.ApiService
import com.aula.androidfoodies.utils.TokenManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

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
                val response = RetrofitInstance.api.fetchNearbyRestaurants(geocodeRequest, token )
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
    fun likeRestaurant(token: String, request: RestaurantRequest){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                RetrofitInstance.api.likeRestaurant(token, request)
            } catch (e: Exception) {
                Log.e("LikeRestaurantError", "Error liking restaurant", e)
            }
        }
    }



}

