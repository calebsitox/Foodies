package com.aula.androidfoodies.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.aula.androidfoodies.retrofit.RetrofitInstance
import com.aula.androidfoodies.viewmodel.AuthViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ChangePassword(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Nueva contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {

                if (newPassword == confirmPassword) {
                    val email = authViewModel.email
                    val inputCode = authViewModel.inputCode
                    Log.d("ChangePassword", "Email: ${authViewModel.email}, Code: ${authViewModel.inputCode}")
                    authViewModel.changePassword(

                        newPassword = newPassword,
                        onSuccess = { message ->
                            successMessage = "¡Cambio de contraseña exitoso!"
                            errorMessage = ""
                            navController.navigate("successScreen")
                        }, onError = { error ->
                            errorMessage = error
                            successMessage = ""
                        }
                    )
                } else {
                    errorMessage = "Las contraseñas no coinciden"
                    successMessage = ""
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cambiar Contraseña")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

