package com.example.myapplication.ui.theme.community

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommunityViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = CommunityDataBase.getDataBase(application).communityPostDao()
    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    init {
        viewModelScope.launch {
            observeCombinedPosts()
        }
    }

    private suspend fun loadAssetPosts(context: Application): List<CommunityPost> {
        return withContext(Dispatchers.IO) {
            val json = context.assets.open("community_post.json")
                .bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<CommunityPost>>() {}.type
            Gson().fromJson(json, type)
        }
    }

    private suspend fun observeCombinedPosts() {
        val context = getApplication<Application>()
        val assetPosts = loadAssetPosts(context)

        dao.getAllPosts().collectLatest { dbEntities ->
            val dbPosts = dbEntities.map { it.toCommunityPost() }
            _posts.value = assetPosts + dbPosts
        }
    }

    fun setPosts(newPosts: List<CommunityPost>) {
        _posts.value = newPosts
    }
}

fun CommunityPostEntity.toCommunityPost() = CommunityPost(
    id = this.userId,
    pfp = this.pfp,
    intro = this.intro,
    title = this.title,
    post = this.post,
    postPic = this.postPic,
    genre = this.genre
)
