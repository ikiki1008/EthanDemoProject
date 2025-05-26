package com.example.myapplication.dataclass

import com.example.myapplication.domain.HelloResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("/api/hello")
    suspend fun getHello(@Header("Authorization") authHeader: String): Response<HelloResponse>
}
