package com.aula.androidfoodies

import LoginScreen
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aula.androidfoodies.ui.theme.AndroidFoodiesTheme
import com.aula.androidfoodies.ui.theme.LocationSearchScreen
import com.aula.androidfoodies.ui.theme.RegisterScreen
import com.aula.androidfoodies.ui.theme.SendEmailScreen
import com.aula.androidfoodies.viewmodel.AuthViewModel
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel
import com.google.android.gms.location.LocationServices

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val authViewModel: AuthViewModel by viewModels()
    private val locationViewModel: AutocompleteViewModel by viewModels()  // Inject ViewModel

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
            composable("login") { LoginScreen(navController, authViewModel, locationViewModel) }
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

                        locationViewModel.updateLocation(latitude, longitude)
                        println("Latitud: $latitude, Longitud: $longitude")
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



}
