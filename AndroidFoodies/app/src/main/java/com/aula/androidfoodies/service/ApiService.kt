package com.aula.androidfoodies.service

import com.aula.androidfoodies.model.LoginRequest
import com.aula.androidfoodies.model.RegisterRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body;
import retrofit2.http.POST;


interface ApiService {
    @POST("api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Response<String>

    @POST("api/auth/register")
    fun register(@Body registerRequest: RegisterRequest): Response<String>
}