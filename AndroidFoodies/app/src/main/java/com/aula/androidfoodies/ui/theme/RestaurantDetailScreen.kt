package com.aula.androidfoodies.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    navController: NavController,
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
            placeDetail.value = viewModel.fetchPlaceDetails(token, request)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFFFF8E1) // Fondo naranja claro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFF8E1))
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

// Título y dirección
            Text(text = name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = address, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

// Valoración y Like en la misma fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Valoración: $rating", style = MaterialTheme.typography.bodyMedium)

                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        val request = RestaurantRequest(username, lat, lon)

                        if (isLiked) {
                            viewModel.likeRestaurant(token.toString(), request)
                            CoroutineScope(Dispatchers.Main).launch {
                                snackbarHostState.showSnackbar("Has dado like a este restaurante")
                            }
                        } else {
                            viewModel.unlikeRestaurant(token.toString(), request)
                            CoroutineScope(Dispatchers.Main).launch {
                                snackbarHostState.showSnackbar("Has quitado el like de este restaurante")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

// Rango de precios
            Text(
                text = "Rango de precios: ${placeDetail.value?.priceLevelDescription ?: "Desconocido"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

// Sección de reseñas
            Text(text = "Reseñas", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            placeDetail.value?.reviews?.forEach { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2)) // naranja claro
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "⭐ ${review.rating} - ${review.authorName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFBF360C) // un naranja oscuro para contraste
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        review.text?.let {
                            Text(text = it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    openInGoogleMaps(context, lat, lon)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800), // Naranja fuerte
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Ver en mapa")
            }
        }
    }
}
fun openInGoogleMaps(context: Context, lat: Double, lon: Double) {
    val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(Restaurante)")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}


