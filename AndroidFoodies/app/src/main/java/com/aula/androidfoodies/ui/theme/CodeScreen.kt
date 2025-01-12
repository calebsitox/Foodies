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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.aula.androidfoodies.viewmodel.AuthViewModel

@Composable
fun CodeScreen(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ingrese el código de 6 dígitos",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = code,
            onValueChange = {
                if (it.length <= 12) {
                    code = it
                }
            },
            label = { Text("Código") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.confirmation(
                    email = email,
                    code = code,
                    onSuccess = { message ->
                        navController.navigate("success")
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
}
@Preview(showBackground = true)
@Composable
fun CodeScreenPreview() {
    CodeScreen(navController = rememberNavController())
}
