package com.aula.androidfoodies.viewmodel

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aula.androidfoodies.model.GeocodeRequest
import com.aula.androidfoodies.model.GeocodeResponse
import com.aula.androidfoodies.model.GeocodeResponseDb
import com.aula.androidfoodies.retrofit.RetrofitInstance
import com.aula.androidfoodies.service.ApiService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationUserViewModel(private val apiService: ApiService) : ViewModel() {

    private val _geocodeResponse = MutableLiveData<GeocodeResponseDb>()
    val geocodeResponse: LiveData<GeocodeResponseDb> get() = _geocodeResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getLastLocation(context: Context) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        // Envía las coordenadas al backend
                        sendCoordinatesToDataBase("your_token", 123L, GeocodeRequest(latitude, longitude))
                        Log.d("Location", "Latitud: $latitude, Longitud: $longitude")
                    } else {
                        _errorMessage.value = "No se pudo obtener la ubicación."
                    }
                }
                .addOnFailureListener { e ->
                    _errorMessage.value = "Error al obtener la ubicación: ${e.message}"
                }
        } catch (e: SecurityException) {
            _errorMessage.value = "Error de seguridad: ${e.message}"
        }
    }

    private fun sendCoordinatesToDataBase(userToken: String, userId: Long, request: GeocodeRequest) {
        viewModelScope.launch {
            try {
                val call = apiService.sendCoordinatesToDataBase(userToken, userId, request)
                call.enqueue(object : Callback<GeocodeResponseDb> {
                    override fun onResponse(call: Call<GeocodeResponseDb>, response: Response<GeocodeResponseDb>) {
                        if (response.isSuccessful) {
                            _geocodeResponse.value = response.body()
                        } else {
                            _errorMessage.value = "Error en la respuesta del servidor: ${response.message()}"
                        }
                    }

                    override fun onFailure(call: Call<GeocodeResponseDb>, t: Throwable) {
                        _errorMessage.value = "Error al conectar con el servidor: ${t.message}"
                    }
                })
            } catch (e: Exception) {
                _errorMessage.value = "Excepción: ${e.message}"
            }
        }
    }
}