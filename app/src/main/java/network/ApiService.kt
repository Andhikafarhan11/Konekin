package com.example.konekin.network

import com.example.konekin.model.Data
import com.example.konekin.model.UserRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("employees")
    fun getAllUsers(): Call<List<Data>>
    @GET("employees/{id}")
    fun getUserById(@Path("id") id: Int): Call<Data>
    @POST("employees")
    fun createUser(@Body request: UserRequest): Call<Data>
    @PUT("employees/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Body request: UserRequest
    ): Call<Data>
    @DELETE("employees/{id}")
    fun deleteUser(@Path("id") id: Int): Call<Data>
}