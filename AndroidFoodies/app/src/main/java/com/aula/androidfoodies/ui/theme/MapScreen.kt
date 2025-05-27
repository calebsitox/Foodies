package com.aula.androidfoodies.ui.theme

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aula.androidfoodies.BottomNavItem
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aula.androidfoodies.R
import com.aula.androidfoodies.utils.TokenManager
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties


@Composable
fun MapScreen(navController: NavHostController, locationViewModel: AutocompleteViewModel, context: Context = LocalContext.current) {

    val locationState by locationViewModel.location.collectAsState()
    val selectedIndex = remember { mutableStateOf(2) } // índice actual (ej: Map)
    val items = listOf(
      BottomNavItem.Profile,
        BottomNavItem.Favorites,
       BottomNavItem.Map
    )
    val token = TokenManager.getToken(context)
    val defaultPosition = LatLng(40.4168, -3.7038)
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }
    var restaurants = locationViewModel.restaurants.value
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }


    LaunchedEffect(locationState) {
        locationState?.let { (lat, lon) ->
            val position = LatLng(lat, lon)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 15f)
        }
    }



    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = {
                            Text(
                                item.label,
                                style = TextStyle(
                                    fontFamily = fontFoodiess,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )
                        },
                        selected = selectedIndex.value == index,
                        onClick = {
                            selectedIndex.value = index
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapStyleOptions = mapStyleOptions)
        ) {
            val markerPosition = locationState?.let { LatLng(it.first, it.second) } ?: defaultPosition
            Marker(
                state = MarkerState(position = markerPosition),
                title = "Position",
                snippet = "Aquí estás"
            )

            // ✅ DENTRO del GoogleMap: dibujar restaurantes
            restaurants.forEach { restaurant ->
                val lat = restaurant["latitude"]
                val lon = restaurant["longitude"]

                if (lat != null && lon != null) {
                    Marker(
                        state = MarkerState(position = LatLng(lat.toDouble(), lon.toDouble())),
                        title = restaurant["name"],
                        snippet = "NearRestaurant"
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp), // espacio desde el bottom nav
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    locationState?.let { (lat, lon) ->
                        token?.let {
                            locationViewModel.fetchNearbyRestaurants(lat, lon, it)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f) // 80% del ancho
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6F00)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Encontrar restaurantes cerca", color = Color.White)
            }
        }


    }
}


