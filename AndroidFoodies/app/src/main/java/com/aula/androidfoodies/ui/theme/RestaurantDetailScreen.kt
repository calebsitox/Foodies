package com.aula.androidfoodies.ui.theme

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aula.androidfoodies.R
import com.aula.androidfoodies.model.GeocodeRequest
import com.aula.androidfoodies.model.PlaceDetailResponse
import com.aula.androidfoodies.model.RestaurantRequest
import com.aula.androidfoodies.utils.TokenManager
import com.aula.androidfoodies.viewmodel.AuthViewModel
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun RestaurantDetailScreen(
    place: Map<String, String>,
    viewModel: AutocompleteViewModel,
    authViewModel: AuthViewModel,
    context: Context = LocalContext.current
) {
    val name = place["name"] ?: "Sin nombre"
    val address = place["address"] ?: "Sin dirección"
    val rating = place["rating"] ?: "Sin valoración"
    val photoRef = place["photoReference"]
    val lat = place["latitude"]?.toDoubleOrNull() ?: 0.0
    val lon = place["longitude"]?.toDoubleOrNull() ?: 0.0
    val username = authViewModel.loadUsername(context)
    val photoUrl = photoRef?.let { viewModel.buildPhotoUrl(it) }

    fun isRestaurantLiked(
        place: Map<String, String>,
        likedList: List<Map<String, String>>
    ): Boolean {
        val placeLat = place["latitude"]
        val placeLon = place["longitude"]

        return likedList.any { liked ->
            liked["latitude"] == placeLat && liked["longitude"] == placeLon
        }
    }

    val likedRestaurants = viewModel.likedRestaurants.value

    var isLiked = isRestaurantLiked(place, likedRestaurants)
    val snackbarHostState = remember { SnackbarHostState() }
    val token = TokenManager.getToken(context)
    val request = GeocodeRequest(lat, lon)

    val placeDetail = remember { mutableStateOf<PlaceDetailResponse?>(null) }

    LaunchedEffect(Unit) {
        if (token != null) {
            placeDetail.value = viewModel.fetchPlaceDetails(token, GeocodeRequest(lat, lon))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Imagen
            if (placeDetail.value?.photos?.isNotEmpty() == true) {
                RestaurantImageCarousel(placeDetail.value?.photos!!)
            } else {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Foto del restaurante",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.cancel),
                    error = painterResource(R.drawable.cancel)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = address, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Valoración: $rating", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Like
            IconButton(onClick = {
                isLiked = !isLiked
                val request = RestaurantRequest(username, lat, lon)

                if (isLiked) {
                    viewModel.likeRestaurant(token.toString(), request)
                    // Mostrar mensaje de confirmación
                    CoroutineScope(Dispatchers.Main).launch {
                        snackbarHostState.showSnackbar("Has dado like a este restaurante")
                    }
                } else {
                    viewModel.unlikeRestaurant(token.toString(), request)
                    CoroutineScope(Dispatchers.Main).launch {
                        snackbarHostState.showSnackbar("Has quitado el like de este restaurante")
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Gray
                )
            }
        }
    }
}



