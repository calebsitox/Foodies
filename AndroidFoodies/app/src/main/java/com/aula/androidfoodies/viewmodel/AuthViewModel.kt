package com.aula.androidfoodies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aula.androidfoodies.model.LoginRequest
import com.aula.androidfoodies.model.RegisterRequest
import com.aula.androidfoodies.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {

    fun login(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error en login: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Error de red: ${e.localizedMessage}")
            } catch (e: HttpException) {
                onError("Error HTTP: ${e.localizedMessage}")
            }
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.register(RegisterRequest(name, email, password))
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error en registro: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Error de red: ${e.localizedMessage}")
            } catch (e: HttpException) {
                onError("Error HTTP: ${e.localizedMessage}")
            }
        }
    }
}
