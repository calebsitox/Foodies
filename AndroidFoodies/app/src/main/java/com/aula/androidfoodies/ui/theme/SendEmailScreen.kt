package com.aula.androidfoodies.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aula.androidfoodies.viewmodel.AuthViewModel

@Composable
fun SendEmailScreen(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.saveEmail(email) // Guardar el email
                authViewModel.sendEmail(email = email, onSuccess = { message ->
                    Log.d("Email Send Success", message)
                    navController.navigate("code")
                },
                    onError = { error ->
                        Log.e("Error Send Email", error)

                    })

            },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
        ) {
            Text("Enviar Correo")
        }

    }

}
@Preview(showBackground = true)
@Composable
fun SendEmailScreenPreview() {
    SendEmailScreen(navController = rememberNavController())
}

