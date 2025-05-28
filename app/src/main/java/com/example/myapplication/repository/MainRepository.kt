package com.example.myapplication.repository

import android.util.Base64
import com.example.myapplication.dataclass.ApiService
import com.example.myapplication.domain.MessageFromSpring
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService : ApiService){

    suspend fun getMessageFromSpirng(): MessageFromSpring {
        val credentials = "testuser:testpass"
        val authHeader = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val response = apiService.getHello(authHeader)
        return if (response.isSuccessful) {
            MessageFromSpring(response.body()?.message ?: "응답이 비어있어요.")
        } else {
            MessageFromSpring("서버 오류: ${response.code()}")
        }
    }
}
