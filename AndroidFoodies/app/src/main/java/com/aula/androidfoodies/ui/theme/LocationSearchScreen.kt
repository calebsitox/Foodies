package com.aula.androidfoodies.ui.theme

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.aula.androidfoodies.model.RestaurantRequest
import com.aula.androidfoodies.utils.TokenManager
import com.aula.androidfoodies.viewmodel.AuthViewModel
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen(
    viewModel: AutocompleteViewModel = viewModel(),
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val searchQuery = remember { mutableStateOf("") }
    val restaurants = viewModel.restaurants.value
    val context = LocalContext.current
    val token = TokenManager.getToken(context)
    val isListVisible = remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        // 1) Scrim transparente
        scrimColor = Color.Transparent,

        drawerContent = {
            // Tu menú lateral
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text("Menú", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                DrawerItem("Perfil", Icons.Default.Person) { /*nav*/ }
                DrawerItem("Ajustes", Icons.Default.Settings) { /*nav*/ }
                DrawerItem("Mis Me Gusta", Icons.Default.Favorite) { /*nav*/ }
                DrawerItem("Mapa Cercano", Icons.Default.Settings) { /*nav*/ }
            }
        }
    ) {
        // Aquí va tu Scaffold dentro del ModalNavigationDrawer
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Foodies App") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)  // evita solapamientos con la AppBar
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
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))


                // Lista de sugerencias de autocompletado
                LazyColumn {
                    if (isListVisible.value) {
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
                var favorito by remember { mutableStateOf(false) }

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
                                favorito = !favorito
                                val username = authViewModel.loadUsername(context)
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
                                        val photoUrl = buildPhotoUrl(imageUrl)
                                        Log.d("ImageURL", "Generated URL: $url")

                                        DisplayPhoto(
                                            photoUrl = photoUrl,
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

                                    // Valoracion del restaurante
                                    Text(
                                        text = place["rating"] ?: "Sin valoracion",
                                        style = MaterialTheme.typography.bodySmall
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
                                            viewModel.likeRestaurant(token.toString(), request)


                                        },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Like",
                                            tint = if (isSelected) Color.Red else Color.Gray
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
        val painter = rememberAsyncImagePainter(
            model = photoUrl,
            placeholder = painterResource(com.google.android.libraries.places.R.drawable.quantum_ic_clear_grey600_24), // Optional placeholder
            error = painterResource(com.google.android.libraries.places.R.drawable.quantum_ic_clear_grey600_24) // Fallback on error
        )

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



    @Composable
    fun DrawerItem(
        title: String,
        icon: ImageVector,
        onClick: (String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }


