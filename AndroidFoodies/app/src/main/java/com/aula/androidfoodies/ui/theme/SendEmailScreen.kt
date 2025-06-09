package com.aula.androidfoodies.ui.theme

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TaskAlt
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aula.androidfoodies.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SendEmailScreen(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    var email by rememberSaveable { mutableStateOf("") }
    var inputCode by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var stage by rememberSaveable { mutableStateOf("email") }
    var successMessage by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(true) }
    var otpCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (stage) {
            "email" -> {

                Text(
                    text = "Recuperation Email",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = fontFoodiess
                    ),
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        authViewModel.saveEmail(it)
                    },
                    label = { Text(text  ="Email",
                        style = TextStyle( fontFamily = fontFoodiess)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFFFF9800),
                        cursorColor = Color(0xFFFF9800)
                    )
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
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Enviar Codigo",
                        style = TextStyle( fontFamily = fontFoodiess,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            "code" -> {
                Text(
                    text = "Insert the code we send you",
                    style = TextStyle(
                        fontFamily = fontFoodiess),
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )


                Spacer(modifier = Modifier.height(16.dp))

                OtpCodeInput(
                    otpLength = 6, // Longitud del código
                    onCodeComplete = { code ->
                        otpCode = code
                        authViewModel.saveInputCode(code)
                    }
                )

                if (isError && resultMessage.isNotEmpty()) {
                    Text(
                        text = resultMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Log.d("SingleScreen", "Código ingresado: $otpCode")
                        authViewModel.confirmation(
                            inputCode = otpCode,
                            onSuccess = { message ->
                                stage = "password"
                            },
                            onError = { error ->
                                Log.e("Confirmation Error", error)
                            }
                        )
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
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Confirm Code",
                        style = TextStyle( fontFamily = fontFoodiess,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            "password" -> {

                Text(
                    text = "Change Password",
                    style = TextStyle(
                        fontFamily = fontFoodiess),
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        isError = false
                    },
                    label = { Text(text  ="New Password",
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

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        isError = false
                    },
                    label = { Text(text  ="Confirm Password ",
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
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Check",
                        style = TextStyle( fontFamily = fontFoodiess,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    )
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
                LaunchedEffect(Unit) {
                    delay(3000L) // Espera 3 segundos
                    visible = false
                    delay(500L) // Espera a que termine la animación
                    navController.navigate("login") {
                        popUpTo("passwordChangeSuccess") { inclusive = true }
                    }
                }


                    AnimatedVisibility(
                        visible = visible,
                        exit = fadeOut(animationSpec = tween(durationMillis = 500))
                    ) {
                        Text(
                            text = "¡Contraseña cambiada exitosamente!",
                            style = TextStyle(fontFamily = fontFoodiess),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }


            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SingleScreenPreview() {
    SendEmailScreen(navController = rememberNavController())
}
@Composable
fun OtpCodeInput(
    otpLength: Int = 6,
    onCodeComplete: (String) -> Unit
) {
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val codeChars = remember { mutableStateListOf(*Array(otpLength) { "" }) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        codeChars.forEachIndexed { index, char ->
            OutlinedTextField(
                value = char,
                onValueChange = { newChar ->
                    if (newChar.length <= 1 && newChar.all { it.isDigit() }) {
                        codeChars[index] = newChar
                        // Si hay carácter y no estamos al final, pasar al siguiente campo
                        if (newChar.isNotEmpty() && index < otpLength - 1) {
                            focusRequesters[index + 1].requestFocus()
                        }
                        // Si todos tienen un carácter, notificar el código completo
                        if (codeChars.all { it.length == 1 }) {
                            onCodeComplete(codeChars.joinToString(" "))
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .focusRequester(focusRequesters[index])
                    .clip(RoundedCornerShape(16.dp)),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF9800),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.DarkGray
                )
            )
        }
    }

    // Al iniciar, enfoca el primer campo
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}
