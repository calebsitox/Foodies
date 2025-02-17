package com.aula.androidfoodies.retrofit

import android.content.Context
import com.aula.androidfoodies.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor que añade el token a cada request, si está disponible
class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.getToken(context)
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        if (!token.isNullOrEmpty()) {
            builder.addHeader("Authorization", "Bearer $token")
        }
        val newRequest = builder.build()
        return chain.proceed(newRequest)
    }
}