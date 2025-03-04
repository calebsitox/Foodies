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
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aula.androidfoodies.model.GeocodeRequest
import com.aula.androidfoodies.model.GeocodeResponse
import com.aula.androidfoodies.retrofit.RetrofitInstance
import com.aula.androidfoodies.ui.theme.AndroidFoodiesTheme
import com.aula.androidfoodies.ui.theme.LocationSearchScreen
import com.aula.androidfoodies.ui.theme.RegisterScreen
import com.aula.androidfoodies.ui.theme.SendEmailScreen
import com.aula.androidfoodies.viewmodel.LocationUserViewModel
import com.google.android.gms.location.LocationServices

import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LocationUserViewModel by viewModels()

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
        if (ActivityCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    viewModel.getLastLocation(this) // Delegar la obtención de la ubicación al ViewModel
                } else {
                    println("Permiso de ubicación denegado por el usuario.")
                }
            }
            requestPermissionLauncher.launch(permission)
        } else {
            viewModel.getLastLocation(this) // Delegar la obtención de la ubicación al ViewModel
        }
    }


}
