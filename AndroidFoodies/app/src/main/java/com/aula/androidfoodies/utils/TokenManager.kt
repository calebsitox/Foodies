package com.aula.androidfoodies.utils

import android.content.Context

object TokenManager {
    private const val PREFS_NAME = "app_prefs"
    private const val AUTH_TOKEN_KEY = "auth_token"

    // Guarda el token en SharedPreferences
    fun saveToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(AUTH_TOKEN_KEY, token).apply()
    }

    // Obtiene el token desde SharedPreferences
    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }

    // Borra el token (por ejemplo, al cerrar sesión)
    fun clearToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(AUTH_TOKEN_KEY).apply()
    }
}