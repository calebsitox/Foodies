package com.aula.androidfoodies.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aula.androidfoodies.viewmodel.AuthViewModel

@Composable
fun RestaurantsList(authViewModel: AuthViewModel = viewModel()) {
    val restaurants = authViewModel.restaurants.value

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(restaurants) { place ->
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = place["name"] ?: "Sin nombre", style = MaterialTheme.typography.titleMedium)
                Text(text = place["address"] ?: "Sin dirección", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}