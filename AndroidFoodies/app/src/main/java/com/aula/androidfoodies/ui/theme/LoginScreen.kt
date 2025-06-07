import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aula.androidfoodies.ui.theme.SendEmailScreen
import com.aula.androidfoodies.ui.theme.fontFoodiess
import com.aula.androidfoodies.ui.theme.playwriteFontFamily
import com.aula.androidfoodies.utils.TokenManager
import com.aula.androidfoodies.viewmodel.AuthViewModel
import com.aula.androidfoodies.viewmodel.AutocompleteViewModel


@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    autocompleteViewModel: AutocompleteViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val location by autocompleteViewModel.location.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Title at the top
        Text(
            text = "Foodies",
            style = TextStyle(
                fontFamily = fontFoodiess),
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Login form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    isError = false // Resetea el error al modificar
                },
                label = { Text(text  ="Nombre de Usuario",
                    style = TextStyle( fontFamily = fontFoodiess)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isError = false
                },
                label = { Text(text  ="Contraseña",
                    style = TextStyle( fontFamily = fontFoodiess)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    val visibilityIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = visibilityIcon, contentDescription = null)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFFF9800),
                    cursorColor = Color(0xFFFF9800)
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (isError && resultMessage.isNotEmpty()) {
                Text(
                    text = resultMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        resultMessage = "Email and password cannot be empty"
                    } else {
                        // Llamamos a la función login desde el ViewModel
                        authViewModel.login(
                            username = username,
                            password = password,
                            onSuccess = { token ->
                                resultMessage = "Login successful"
                                TokenManager.saveToken(context, token)
                                Log.i("Auth", "Token: $token")
                                location?.let { (lat, lon) ->
                                    autocompleteViewModel.sendCoordinatesToBackend(lat, lon, token)
                                }
                                authViewModel.saveUsername(context, username)

                                navController.navigate("location")
                            },
                            onError = {
                                resultMessage = "Incorrect username or password"
                                isError = true
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722), // Naranja vibrante
                    contentColor = Color.White // Texto blanco
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 12.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Iniciar sesión",
                    style = TextStyle( fontFamily = fontFoodiess,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = resultMessage)
        }
        Text(
            text = "¿Olvidaste tu contraseña? Enviar correo",
            fontSize = 14.sp,
            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { navController.navigate("sendEmail") })

        // Underlined text for sign up
        Text(
            text = "Don't have an account? Sign up",
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    navController.navigate("register")
                    // TODO: Action to navigate to sign-up screen
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    val fakeViewModel = remember { FakeAuthViewModel() }
    val fakeViewModel1 = remember { FakeAuthViewModel1() }
    LoginScreen(navController = navController, authViewModel = fakeViewModel, autocompleteViewModel = fakeViewModel1 )
}

class FakeAuthViewModel : AuthViewModel() {
    // Sobrescribe los métodos necesarios si hace falta
    // Si usas otras funciones en LoginScreen, las simulas aquí
}

class FakeAuthViewModel1 : AutocompleteViewModel() {
    // Sobrescribe los métodos necesarios si hace falta
    // Si usas otras funciones en LoginScreen, las simulas aquí
}