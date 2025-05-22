package com.aula.androidfoodies.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import com.aula.androidfoodies.R
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties


@Composable
fun MapScreen(navController: NavHostController, locationViewModel: AutocompleteViewModel) {

    val locationState by locationViewModel.location.collectAsState()
    val selectedIndex = remember { mutableStateOf(2) } // índice actual (ej: Map)
    val items = listOf(
      BottomNavItem.Profile,
        BottomNavItem.Favorites,
       BottomNavItem.Map
    )
    val defaultPosition = LatLng(40.4168, -3.7038)
    val context = LocalContext.current
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }

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
        }
    }
}


