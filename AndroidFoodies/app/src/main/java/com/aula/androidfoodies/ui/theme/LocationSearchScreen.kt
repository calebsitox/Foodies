package com.aula.androidfoodies.ui.theme

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.aula.androidfoodies.BottomNavItem
import com.aula.androidfoodies.R
import com.aula.androidfoodies.model.RestaurantRequest
import com.aula.androidfoodies.utils.TokenManager
import com.aula.androidfoodies.viewmodel.AuthViewModel
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel
import com.google.gson.Gson

@Composable
fun LocationSearchScreen(
    viewModel: AutocompleteViewModel = viewModel(),
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val items = listOf(
        BottomNavItem.Profile,
        BottomNavItem.Favorites,
        BottomNavItem.Map
    )
    val selectedIndex = remember { mutableStateOf(1) }
    val searchQuery = remember { mutableStateOf("") }
    var restaurants = viewModel.restaurants.value
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    val isListVisible = remember { mutableStateOf(true) }
    val locationState by viewModel.location.collectAsState()



    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf(
        "🍕 Pizza",
        "🍔 Hamburguesa",
        "🌮 Tacos",
        "🍣 Sushi",
        "🥗 Ensalada",
        "🍜 Ramen",
        "🥞 Pancakes"
    )
    val seleccionadas = remember {
        mutableStateMapOf<String, Boolean>().apply {
            opciones.forEach { put(it, false) }
        }
    }

    var favorito by remember { mutableStateOf(false) }

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
                                    fontFamily = fontFoodiess, // Puedes usar FontFamily.Default, Monospace, etc.
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
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

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
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().onFocusChanged{ focusState ->
                    isListVisible.value = focusState.isFocused
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                )
            )

            Spacer(modifier = Modifier.height(9.dp))

            val allSuggestions = listOf("📍 Mi ubicación")

            // Lista de sugerencias de autocompletado
            LazyColumn {
                if (isListVisible.value) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    searchQuery.value = "📍 Mi ubicación"
                                    isListVisible.value = false
                                    locationState?.let { (lat, lon) ->
                                        if (token != null) {
                                            viewModel.fetchNearbyRestaurants(lat, lon, token)
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFB74D),
                                contentColor = Color.Black
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Text(
                                text = "📍 Mi ubicación",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
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
                                            viewModel.fetchNearbyRestaurants(
                                                latitude,
                                                longitude,
                                                token
                                            )
                                        }


                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFB74D),
                                contentColor = Color.Black),
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

            var expanded by remember { mutableStateOf(false) }


            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(250.dp), // Espaciado entre iconos
                    verticalAlignment = Alignment.CenterVertically // Alinear verticalmente los iconos
                ) {
                    // Botón con icono para mostrar/ocultar filtros
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(48.dp) // Tamaño del icono

                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.List,
                            contentDescription = "Filtros",
                            tint = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            val username = authViewModel.loadUsername(context)
                            favorito = !favorito
                            if (!favorito) {
                            //    viewModel.clearRestaurants()
                            }
                            if (token != null) {
                                viewModel.likedRestaurant(token, username)
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorito",
                            tint = if (favorito) Color.Red else Color.Gray // Cambia color dinámicamente
                        )
                    }
                }

                if (expanded) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var rowWidth = 0.dp
                        val maxRowWidth = 300.dp // Ancho máximo antes de pasar a otra fila
                        var currentRowItems = mutableListOf<String>()

                        opciones.forEach { opcion ->
                            val opcionWidth =
                                opcion.length * 10.dp // Aproximación dinámica del ancho

                            if (rowWidth + opcionWidth > maxRowWidth) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    currentRowItems.forEach { item ->
                                        BotonFiltro(item, seleccionadas)
                                    }
                                }
                                currentRowItems.clear()
                                rowWidth = 0.dp
                            }

                            currentRowItems.add(opcion)
                            rowWidth += opcionWidth
                        }

                        if (currentRowItems.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                currentRowItems.forEach { item ->
                                    BotonFiltro(item, seleccionadas)
                                }
                            }
                        }
                    }
                }
            }


            if (restaurants.isEmpty()) {
                Text(
                    text = "Search more Restaurants!. Try searching for another place.",
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
                                .padding(16.dp)
                                .clickable{
                                    val gson = Gson()
                                    val placeJson = gson.toJson(place)
                                    val encodedPlace = Uri.encode(placeJson)

                                    navController.navigate("restaurantDetail/$encodedPlace")
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFA726)
                            ),
                            elevation = CardDefaults.cardElevation(10.dp),
                            shape = RoundedCornerShape(20.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Imagen desde URL
                                val imageUrl = place["photoReference"] // URL de la imagen
                                if (!imageUrl.isNullOrEmpty() && !token.isNullOrEmpty()) {
                                    val url = viewModel.fetchPhotoUrl(
                                        photoReference = imageUrl,
                                        token = token
                                    )
                                    val photoUrl = buildPhotoUrl(imageUrl)
                                    Log.d("ImageURL", "Generated URL: $url")

                                    DisplayPhoto(
                                        photoUrl = photoUrl,
                                        contentDescription = "Imagen del restaurante",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Nombre del restaurante
                                Text(
                                    text = place["name"] ?: "Sin nombre",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Dirección del restaurante
                                Text(
                                    text = place["address"] ?: "Sin dirección",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                // Valoracion del restaurante
                                Text(
                                    text = "⭐ ${place["rating"] ?: "Sin valoración"}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFFfbc02d), // color dorado tipo estrella
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                var isSelected by remember { mutableStateOf(false) }

                                IconButton(
                                    onClick = {
                                        isSelected = !isSelected
                                        val latitude = place["latitude"]
                                        val longitude = place["longitude"]
                                        val username = authViewModel.loadUsername(context)
                                        val request = RestaurantRequest(
                                            username,
                                            latitude.toString().toDouble(),
                                            longitude.toString().toDouble()
                                        )
                                        if (isSelected) {
                                            // Llamada para marcar como favorito (like)
                                            viewModel.likeRestaurant(token.toString(), request)
                                        } else {
                                            // Llamada para desmarcar como favorito (unlike)
                                            viewModel.unlikeRestaurant(token.toString(), request)
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Like",
                                        tint = if (favorito) Color.Red else if (isSelected) Color.Red else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// Forma CORRECTA de construir la URL con parámetros
fun buildPhotoUrl(photoRef: String): String {
    return Uri.parse("https://maps.googleapis.com/maps/api/place/photo")
        .buildUpon()
        .appendQueryParameter("maxwidth", "400")
        .appendQueryParameter("photoreference", photoRef) // No necesitas URLEncoder aquí
        .appendQueryParameter("key", "AIzaSyCNSEbqAUraUirf4YqRBbdxflyysTWWx6c") // Tu API key
        .build()
        .toString()
}

@Composable
fun DisplayPhoto(
    photoUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val painter = if (!photoUrl.isNullOrEmpty()) {
        rememberAsyncImagePainter(
            model = photoUrl,
            placeholder = painterResource(R.drawable.cancel), // puedes usar un recurso tuyo o quitarlo
            error = painterResource(R.drawable.cancel) // si falla la carga
        )
    } else {
        painterResource(R.drawable.cancel) // si la URL está vacía o nula
    }

    Image(
        painter = painter,
        contentDescription = "Restaurant Photo",
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    )
}

@Composable
fun BotonFiltro(texto: String, seleccionadas: MutableMap<String, Boolean>) {
    val seleccionado = seleccionadas[texto] ?: false
    Button(
        onClick = { seleccionadas[texto] = !seleccionado },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) Color.LightGray else Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .wrapContentWidth()
            .height(30.dp)
    ) {
        Text(texto, color = Color.Black, fontSize = 12.sp)
    }
}







