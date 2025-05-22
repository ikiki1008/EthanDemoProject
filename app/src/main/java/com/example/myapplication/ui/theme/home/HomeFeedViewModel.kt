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

    private val _allPosts = mutableStateListOf<HomeMainFeedUiModel>()
    val allPosts: List<HomeMainFeedUiModel> get() = _allPosts

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
            //스켈레톤 먼저 추가
            repeat(20) {
                _allPosts.add(HomeMainFeedUiModel(null, isSkeleton = true))
            }

            delay(300)

            //스켈레톤 제거
            repeat(20) {
                _allPosts.removeLast()
            }

            val currentSize = _allPosts.count { !it.isSkeleton }
            val next = allJsonPosts.drop(currentSize).take(4)

            if (next.isEmpty()) {
                endReached = true
            } else {
                _allPosts.addAll(next.map { HomeMainFeedUiModel(it) })
            }

            isLoading = false
        }
    }
}
