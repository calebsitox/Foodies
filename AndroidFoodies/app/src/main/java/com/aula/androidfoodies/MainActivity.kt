package com.aula.androidfoodies

import LoginScreen
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aula.androidfoodies.ui.theme.AndroidFoodiesTheme
import com.aula.androidfoodies.ui.theme.LocationSearchScreen
import com.aula.androidfoodies.ui.theme.MapScreen
import com.aula.androidfoodies.ui.theme.RegisterScreen
import com.aula.androidfoodies.ui.theme.RestaurantDetailScreen
import com.aula.androidfoodies.ui.theme.SendEmailScreen
import com.aula.androidfoodies.viewmodel.AuthViewModel
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val authViewModel: AuthViewModel by viewModels()
    private val locationViewModel: AutocompleteViewModel by viewModels()  // Inject ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
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
            composable("location") { LocationSearchScreen( locationViewModel, authViewModel, navController) }
            composable("map") { MapScreen(navController, locationViewModel)}
            composable("restaurantDetail/{placeJson}",
                arguments = listOf(navArgument("placeJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("placeJson")
                val place: Map<String, String> = Gson().fromJson(json, object : TypeToken<Map<String, String>>() {}.type)

                RestaurantDetailScreen(place, locationViewModel, authViewModel)
            }
        }
    }

    private fun checkLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no está concedido, solicitarlo
            requestPermissionLauncher.launch(permission)
        } else {
            // Si el permiso ya está concedido, llamar a la función que necesita la ubicación
            getCurrentLocation()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Si el usuario concede el permiso, obtener la ubicación
                getCurrentLocation()
            } else {
                // Si el usuario no concede el permiso, mostrar un mensaje o manejar el rechazo
                println("Permiso de ubicación denegado por el usuario.")
            }
        }

    private fun getLastLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("permisos","Permisos de ubicación no concedidos.Permisos de ubicación no concedidos.")
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Aquí actualizas el ViewModel
                    locationViewModel.updateLocation(latitude, longitude)

                    Log.i("Ubicacion", "Última ubicación: Latitud: $latitude, Longitud: $longitude")
                } else {
                    Log.i("Ubicacion", "No se encontró última ubicación.")
                }
            }
            .addOnFailureListener {
                Log.e("Error", "Error al obtener la ubicación: ${it.message}")
            }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val cancellationToken = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                locationViewModel.updateLocation(latitude, longitude)
                Log.i("Ubicacion", "Ubicación actual: Lat: $latitude, Lon: $longitude")
            } else {
                Log.i("Ubicacion", "No se encontró la ubicación actual.")
            }
        }.addOnFailureListener {
            Log.e("Ubicacion", "Error obteniendo ubicación actual", it)
        }
    }



}
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
    object Favorites : BottomNavItem("location", Icons.Default.Search, "Busca")
    object Map : BottomNavItem("map", Icons.Default.Place, "Mapa")
}
