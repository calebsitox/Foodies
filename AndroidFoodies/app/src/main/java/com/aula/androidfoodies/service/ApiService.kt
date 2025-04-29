package com.aula.androidfoodies.service

import com.aula.androidfoodies.model.AddressRequest
import com.aula.androidfoodies.model.AutocompleteResponse
import com.aula.androidfoodies.model.GeocodeRequest
import com.aula.androidfoodies.model.GeocodeResponse
import com.aula.androidfoodies.model.GeocodeResponseToCordenates
import com.aula.androidfoodies.model.LoginRequest
import com.aula.androidfoodies.model.RegisterRequest
import com.aula.androidfoodies.model.RestaurantRequest
import com.aula.androidfoodies.model.Security
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Security>

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<String>

    @PUT("api/check/forgot-password")
    suspend fun forgotPassword(@Query("email") email: String): Response<String>

    @POST("api/check/confirmation")
    fun confirmation(@Query("email") email: String, @Body inputCode: String): Call<String>

    @PUT("api/check/set-password")
    fun setPassword(
        @Query("email") email: String, @Query("inputCode") inputCode: String,
        @Body newPassword: String
    ): Call<String>

    @POST("api/photo")
    suspend fun getUrlPhoto(
        @Body photoReference: String,
        @Header("Authorization") token: String
    ): String

    @POST("api/location/geocode")
    fun sendCoordinates(
        @Body request: GeocodeRequest,
        @Header("Authorization") token: String
    ): Call<GeocodeResponse>

    @POST("/api/places/name/directions")
    suspend fun fetchNearbyRestaurants(
        @Body request: GeocodeRequest,
        @Header("Authorization") token: String
    ): Response<List<Map<String, String>>>

    @GET("/api/autocomplete")
    suspend fun getAutocomplete(
        @Query("input") input: String
    ): Response<AutocompleteResponse>

    @GET("api/geocode/addressToCoordinates")
    fun getCoordinates(@Query("address") request: AddressRequest): Call<GeocodeResponseToCordenates>

    @POST("api/likeRestuarant")
    suspend fun likeRestaurant(
        @Header("Authorization") token: String,
        @Body request: RestaurantRequest
    )

}