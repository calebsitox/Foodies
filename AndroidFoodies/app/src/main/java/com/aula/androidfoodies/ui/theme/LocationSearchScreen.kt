package com.aula.androidfoodies.ui.theme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.aula.androidfoodies.R
import com.aula.androidfoodies.model.GeocodeRequest
import com.aula.androidfoodies.utils.TokenManager
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel

@Composable
fun LocationSearchScreen(
    navController: NavHostController,
    viewModel: AutocompleteViewModel = viewModel()
) {
    val searchQuery = remember { mutableStateOf("") }
    val restaurants = viewModel.restaurants.value
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    val isListVisible = remember { mutableStateOf(true) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { query ->
                searchQuery.value = query
                if (query.isNotEmpty()) {
                    isListVisible.value = true // Show suggestions while typing
                    viewModel.fetchAutocompleteSuggestions(query)
                } else {
                    isListVisible.value = false // Hide suggestions if query is empty
                }
            },
            label = { Text("Buscar dirección") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de sugerencias de autocompletado
        LazyColumn {
            if(isListVisible.value) {
                items(viewModel.suggestions) { suggestion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                // Actualiza el campo de búsqueda y obtiene coordenadas
                                searchQuery.value = suggestion
                                isListVisible.value = false
                                viewModel.fetchCoordinates(suggestion) { latitude, longitude ->
                                    // Usa las coordenadas obtenidas para buscar restaurantes cercanos
                                    if (token != null) {
                                        viewModel.fetchNearbyRestaurants(latitude, longitude, token)
                                    }


                                }
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Text(
                            text = suggestion,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (restaurants.isEmpty()) {
            Text(
                text = "No restaurants found. Try searching for another place.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // Lista de restaurantes obtenidos
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(restaurants) { place ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Imagen desde URL
                            val imageUrl = place["photoReference"] // URL de la imagen
                            if (!imageUrl.isNullOrEmpty() && !token.isNullOrEmpty()) {
                                val url = viewModel.fetchPhotoUrl(
                                    photoReference = imageUrl,
                                    token = token
                                )
                                Log.d("ImageURL", "Generated URL: $url")

                                AsyncImage(
                                    model = url.toString(),
                                    contentDescription = "Imagen del restaurante",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Nombre del restaurante
                            Text(
                                text = place["name"] ?: "Sin nombre",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Dirección del restaurante
                            Text(
                                text = place["address"] ?: "Sin dirección",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun AsyncImage(
    model: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(
        model = model,
        placeholder = painterResource(com.google.android.libraries.places.R.drawable.quantum_ic_clear_grey600_24), // Optional placeholder
        error = painterResource(com.google.android.libraries.places.R.drawable.quantum_ic_clear_grey600_24) // Fallback on error
    )
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
    )
}
