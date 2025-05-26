package com.example.myapplication.ui.theme.dataclass

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("/api/hello")
    suspend fun getHello(@Header("Authorization") authHeader: String): Response<HelloResponse>
}
