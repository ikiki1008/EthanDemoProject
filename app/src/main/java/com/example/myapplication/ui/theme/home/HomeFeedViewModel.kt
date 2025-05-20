package com.example.myapplication.ui.theme.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.theme.dataclass.CreatorPost
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFeedViewModel(application: Application) : AndroidViewModel(application) {

    private val _allPosts = mutableStateListOf<CreatorPost>()
    val allPosts: List<CreatorPost> get() = _allPosts

    private var allJsonPosts: List<CreatorPost> = emptyList()

    var isLoading by mutableStateOf(false)
        private set

    var endReached by mutableStateOf(false)
        private set

    var showShimmering by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            delay(100)
            showShimmering = false
            loadJsonData()
            loadNextItems()
        }
    }

    private fun loadJsonData() {
        val context = getApplication<Application>().applicationContext
        val json = context.assets.open("sample_data.json")
            .bufferedReader().use { it.readText() }
        allJsonPosts = Gson().fromJson(json, object : TypeToken<List<CreatorPost>>() {}.type)
    }

    fun loadNextItems() {
        if (isLoading || endReached || showShimmering) return

        isLoading = true
        viewModelScope.launch {
            delay(500)
            val next = allJsonPosts.drop(_allPosts.size).take(4)
            if (next.isEmpty()) {
                endReached = true
            } else {
                _allPosts.addAll(next)
            }
            isLoading = false
        }
    }
}
