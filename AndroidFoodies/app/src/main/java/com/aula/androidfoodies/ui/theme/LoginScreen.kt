import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
    var resultMessage by remember { mutableStateOf("") }
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
            fontSize = 48.sp,
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
                onValueChange = { username = it },
                label = { Text("User Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))



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
                                resultMessage = "Error: $it"
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
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
