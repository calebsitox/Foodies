package com.aula.androidfoodies.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aula.androidfoodies.model.LoginRequest
import com.aula.androidfoodies.model.RegisterRequest
import com.aula.androidfoodies.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.io.IOException

open class AuthViewModel () : ViewModel() {
    private var _username = mutableStateOf("")
    val username: State<String> get() = _username


    fun loadUsername(context: Context): String{
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        return prefs.getString("username", "") ?: ""
    }


    fun saveUsername(context: Context, newUsername: String) {
        _username.value = newUsername
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("username", newUsername).apply()
        Log.d("AuthViewModel", "Username guardado: $newUsername")
    }

    private var _email = mutableStateOf("")
    val email: State<String> = _email


    private var _inputCode = mutableStateOf("")
    val inputCode: State<String> = _inputCode

    fun saveEmail(newEmail: String) {
        Log.d(
            "AuthViewModel",
            "Guardando email: $newEmail"
        )  // Este log debe mostrar el email que estás guardando
        _email.value = newEmail
    }

    fun saveInputCode(newInputCode: String) {
        Log.d("AuthViewModel", "Guardando código: $newInputCode") // ✅ Verificar si se actualiza
        _inputCode.value = newInputCode
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
        inputCode: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val emailValue = email.value // Guardamos el valor del email

        if (emailValue.isEmpty()) {
            onError("El email está vacío")
            return
        }
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Confirmación con email: ${email.value}, Código: $inputCode")
                val response = RetrofitInstance.api.confirmation(emailValue, inputCode)
                    .awaitResponse()

                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                        ?: onError("El cuerpo de la respuesta es nulo")
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun changePassword(
        newPassword: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d(
                    "*********************CodeScreen",
                    "Email: $email, Code: $inputCode"
                ) // Añadir un log para depurar
                val response =
                    RetrofitInstance.api.setPassword(email.value, inputCode.value, newPassword)
                        .awaitResponse()
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                        ?: onError("El cuerpo de la respuesta es nulo")
                } else {
                    onError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun getEmail(user: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.api.getEmail(user)
                _email.value = result
            } catch (e: Exception) {
                // Manejo de error si lo deseas
            }
        }
    }

}




