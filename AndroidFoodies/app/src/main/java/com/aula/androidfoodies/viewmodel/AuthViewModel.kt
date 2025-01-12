package com.aula.androidfoodies.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aula.androidfoodies.model.LoginRequest
import com.aula.androidfoodies.model.RegisterRequest
import com.aula.androidfoodies.model.Security
import com.aula.androidfoodies.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class AuthViewModel : ViewModel() {
    var email = mutableStateOf("")
        private set

    fun saveEmail(newEmail: String) {
        email.value = newEmail
    }


    fun login(
        username: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        onSuccess(token)
                    } else {
                        onError("Token is null")
                    }
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: IOException) {
                // Problema de red
                onError("Network error: ${e.localizedMessage}")
            } catch (e: HttpException) {
                // Error HTTP
                onError("HTTP error: ${e.localizedMessage}")
            } catch (e: Exception) {
                // Otros errores
                onError("Unknown error: ${e.localizedMessage}")
            }
        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response =
                    RetrofitInstance.api.register(RegisterRequest(username, email, password))
                if (response.isSuccessful) {
                    val responseBody =
                        response.body() ?: return@launch onError("Response body is null")
                    onSuccess(responseBody) // Pasa el cuerpo de la respuesta como String a onSuccess
                } else {
                    onError("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun sendEmail(
        email: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.forgotPassword(email)
                if (response.isSuccessful) {
                    val responseBody =
                        response.body() ?: return@launch onError("Response body is null")
                    onSuccess(responseBody) // Pasa el cuerpo de la respuesta como String a onSuccess
                } else {
                    onError("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun confirmation(
        email: String,
        code: String,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.confirmation(email, code)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onSuccess(responseBody.toBoolean()) // Maneja true o false
                    } else {
                        onError("Response body is null")
                    }
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Unknown error")
            }
        }
    }


}
