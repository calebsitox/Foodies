package com.aula.androidfoodies

import LoginScreen
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.Manifest
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aula.androidfoodies.model.GeocodeRequest
import com.aula.androidfoodies.model.GeocodeResponse
import com.aula.androidfoodies.retrofit.RetrofitInstance
import com.aula.androidfoodies.service.ApiService
import com.aula.androidfoodies.ui.theme.AndroidFoodiesTheme
import com.aula.androidfoodies.ui.theme.LocationSearchScreen
import com.aula.androidfoodies.ui.theme.RegisterScreen
import com.aula.androidfoodies.ui.theme.SendEmailScreen
import com.aula.androidfoodies.viewmodel.AuthViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidFoodiesTheme {
                val navController = rememberNavController()
                NavigationComponent(navController)
            }
        }
        checkLocationPermission()
    }


    @Composable
    fun NavigationComponent(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("sendEmail") { SendEmailScreen(navController) }
            composable("location") { LocationSearchScreen(navController) }
        }
    }

    private fun checkLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    getLastLocation()
                } else {
                    // Maneja el caso en que el usuario no otorga el permiso
                    println("Permiso de ubicación denegado por el usuario.")
                }
            }
            requestPermissionLauncher.launch(permission)
        } else {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        // Envía las coordenadas al backend
                        sendCoordinatesToBackend(latitude, longitude)
                        println("Latitud: $latitude, Longitud: $longitude")
                    } else {
                        println("No se pudo obtener la ubicación.")
                    }
                }
        } catch (e: SecurityException) {
            e.printStackTrace()
            println("Error de seguridad al intentar obtener la ubicación.")
        }
    }

    fun sendCoordinatesToBackend(lat: Double, lon: Double) {
        val request = GeocodeRequest(lat, lon)

        Log.d("API", "Enviando coordenadas: Latitud = $lat, Longitud = $lon")

        RetrofitInstance.api.sendCoordinates(request).enqueue(object : Callback<GeocodeResponse> {
            override fun onResponse(call: Call<GeocodeResponse>, response: Response<GeocodeResponse>) {
                if (response.isSuccessful) {
                    val address = response.body()?.address
                    println("Dirección recibida: $address")
                } else {
                    println("Error en la respuesta del servidor: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                println("Error al conectar con el servidor: ${t.message}")
            }
        })
    }





}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController)
}