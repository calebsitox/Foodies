package com.aula.androidfoodies.ui.theme

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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel

@Composable
fun LocationSearchScreen(navController: NavHostController, viewModel: AutocompleteViewModel = viewModel()) {
    val searchQuery = remember { mutableStateOf("") }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { query ->
                searchQuery.value = query
                // Llamar a la función de autocompletado
                viewModel.fetchAutocompleteSuggestions(query)
            },
            label = { Text("Buscar dirección") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar sugerencias
        LazyColumn {
            items(viewModel.suggestions) { suggestion ->
                Text(text = suggestion, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
