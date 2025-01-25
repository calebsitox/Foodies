package com.aula.androidfoodies.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aula.androidfoodies.viewmodel.AuthViewModel

@Composable
fun SendEmailScreen(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    var email by rememberSaveable { mutableStateOf("") }
    var inputCode by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var stage by rememberSaveable { mutableStateOf("email") }
    var successMessage by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (stage) {
            "email" -> {
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        authViewModel.saveEmail(it)
                    },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Log.d("SingleScreen", "Email: $email")
                        authViewModel.sendEmail(email = email, onSuccess = { message ->
                            Log.d("Email Send Success", message)
                            stage = "code"
                        },
                            onError = { error ->
                                Log.e("Error Send Email", error)
                            })
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enviar Correo")
                }
            }

            "code" -> {
                Text(
                    text = "Ingrese el código de 6 dígitos",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = inputCode,
                    onValueChange = {
                        inputCode = it
                        authViewModel.saveInputCode(it)
                    },
                    label = { Text("Código") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Log.d("SingleScreen", "Código ingresado: $inputCode")
                        authViewModel.confirmation(
                            inputCode = inputCode,
                            onSuccess = { message ->
                                stage = "password"
                            },
                            onError = { error ->
                                Log.e("Confirmation Error", error)
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirmar Código")
                }
            }

            "password" -> {
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
                            val emailValue = authViewModel.email.value
                            val inputCodeValue = authViewModel.inputCode.value
                            Log.d("SingleScreen", "Email: $emailValue, Code: $inputCodeValue")
                            authViewModel.changePassword(
                                newPassword = newPassword,
                                onSuccess = { message ->
                                    successMessage = "¡Cambio de contraseña exitoso!"
                                    errorMessage = ""
                                    stage = "success"
                                }, onError = { error ->
                                    errorMessage = error
                                    successMessage = ""
                                }
                            )
                        } else {
                            errorMessage = "Las contraseñas no coinciden"
                            successMessage = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
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

            "success" -> {
                Text(
                    text = "¡Cambio de contraseña exitoso!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SingleScreenPreview() {
    SendEmailScreen(navController = rememberNavController())
}

