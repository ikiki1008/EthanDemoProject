package com.example.myapplication.presentation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = MainRepository()

    private val _helloText = mutableStateOf("")
    val helloText: State<String> = _helloText

    fun fetchData() {
        viewModelScope.launch {
            try {
                val result = repository.getMessageFromSpirng()
                _helloText.value = result.message
            } catch (e: Exception) {
                _helloText.value = "예외 발생: ${e.localizedMessage ?: "알 수 없는 오류"}"
            }
        }
    }
}
