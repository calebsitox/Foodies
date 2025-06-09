package com.aula.androidfoodies.ui.theme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aula.androidfoodies.BottomNavItem
import com.aula.androidfoodies.R
import com.aula.androidfoodies.viewmodel.AuthViewModel

@Composable
fun PerfilScreen(navController: NavHostController, authViewModel: AuthViewModel) {

    val items = listOf(
        BottomNavItem.Profile,
        BottomNavItem.Favorites,
        BottomNavItem.Map
    )
    val selectedIndex = remember { mutableStateOf(0) }
    val username = authViewModel.username.value
    val email = authViewModel.email.value

    LaunchedEffect(Unit) {
        authViewModel.getEmail(username)
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de perfil
            Image(
                painter = painterResource(id = R.drawable.pig),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Nombre
            UserNameText(
                username = username.toString(),
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            UserEmailText(
                email = email.toString(),
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
            // Correo electrónico

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para editar perfil
            Column(modifier = Modifier.fillMaxWidth()) {

                PerfilOptionRow("Editar perfil") {
                    // Acción editar perfil
                }

                PerfilOptionRow("Cambiar contraseña") {
                    navController.navigate("sendEmail"){
                        launchSingleTop = true
                    }
                }


                PerfilOptionRow("Resgiter") {
                    navController.navigate("register") {
                        launchSingleTop = true
                    }

                }

                PerfilOptionRow("Cerrar sesión") {
                    System.exit(0)
                }

                PerfilOptionRow("Borrar cuenta", textColor = Color.Red) {
                    // Acción borrar cuenta
                }
            }
        }
    }
}

@Composable
fun PerfilOptionRow(
    title: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Log.d("PerfilOptionRow", "Clic en $title") // Log para depuración
                onClick()
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun UserNameText(
    username: String?,
    defaultText: String = "Usuario",
    textColor: Color = Color.Black,
    backgroundColor: Color = Color.Transparent,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = username ?: defaultText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun UserEmailText(
    email: String?,
    modifier: Modifier = Modifier,
    defaultText: String = "Sin email registrado",
    isValidColor: Color = Color(0xFF4285F4), // Azul Google
    isInvalidColor: Color = Color.Red
) {
    val emailText = email?.takeIf { it.isNotBlank() } ?: defaultText
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()

    Text(
        text = emailText,
        modifier = modifier,
        fontSize = 16.sp,
        fontWeight = if (isEmailValid) FontWeight.Normal else FontWeight.SemiBold,
        color = if (email == null) Color.Gray
        else if (isEmailValid) isValidColor
        else isInvalidColor,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}