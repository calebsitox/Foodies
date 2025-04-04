package com.aula.androidfoodies.ui.theme

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { query ->
                searchQuery.value = query
                // Llamada al autocompletado
                viewModel.fetchAutocompleteSuggestions(query)
            },
            label = { Text("Buscar dirección") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de sugerencias de autocompletado
        LazyColumn {
            items(viewModel.suggestions) { suggestion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            // Actualiza el campo de búsqueda y obtiene coordenadas
                            searchQuery.value = suggestion
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

        Spacer(modifier = Modifier.height(16.dp))

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
                        Text(
                            text = place["name"] ?: "Sin nombre",
                            style = MaterialTheme.typography.titleMedium
                        )
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
