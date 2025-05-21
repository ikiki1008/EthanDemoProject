package com.example.myapplication.ui.theme.community

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.theme.dataclass.CommunityPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class CommunityViewModel(application: Application) : AndroidViewModel(application) {
    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    init {
        loadInitialPosts()
    }

    private fun loadInitialPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val gson = Gson()

            // 1. assets의 초기 JSON 데이터 읽기
            val assetPosts: List<CommunityPost> = context.assets.open("community_post.json")
                .bufferedReader()
                .use { reader ->
                    gson.fromJson(reader, object : TypeToken<List<CommunityPost>>() {}.type)
                }

            // 2. 내부 저장소 파일이 있으면 읽기 (없으면 빈 리스트)
            val file = File(context.filesDir, "community_post.json")
            val userPosts: List<CommunityPost> = if (file.exists()) {
                val json = file.readText()
                gson.fromJson(json, object : TypeToken<List<CommunityPost>>() {}.type)
            } else {
                emptyList()
            }

            // 3. 합쳐서 ViewModel에 저장 (기존 게시글 + 사용자 게시글)
            _posts.value = assetPosts + userPosts
        }
    }

    fun setPosts(posts: List<CommunityPost>) {
        _posts.value = posts
    }

    fun addPost(newPost: CommunityPost) {
        _posts.value = _posts.value + newPost
    }
}
