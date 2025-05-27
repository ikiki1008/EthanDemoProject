package com.example.myapplication.ui.theme.community

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dataclass.CommunityDataBase
import com.example.myapplication.domain.CommunityPost
import com.example.myapplication.dataclass.CommunityPostEntity
import com.example.myapplication.repository.CommunityRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File

//카테고리 이미지 데이터 모델
data class CategoryImage(val pic: String, val title: String, val subTitle: String)

data class HashTagItem (val title: String, val subTitle: String, val img1: String, val img2 : String,
    val img3 : String, val img4 : String)

data class ChallengeData(
    val pic: String,
    val title: String,
    val subTitle: String
)

data class HashTagData(
    val title: String,
    val subText: String,
    val img1: String,
    val img2 : String,
    val img3 : String,
    val img4 : String
)

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = CommunityDataBase.getDataBase(application).communityPostDao()
    private val repository = CommunityRepository()
    private val context = getApplication<Application>()

    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    private val _categoryImages = MutableStateFlow<List<CategoryImage>>(emptyList())
    val categoryImages: StateFlow<List<CategoryImage>> = _categoryImages.asStateFlow()

    private val _hashTagDatas = MutableStateFlow<List<HashTagData>>(emptyList())
    val hashTagDatas: StateFlow<List<HashTagData>> = _hashTagDatas.asStateFlow()

    val showShimmering = mutableStateOf(true)

    init {
        viewModelScope.launch { observeCombinedPosts() }
        viewModelScope.launch { loadCategoryImages() }
        viewModelScope.launch { loadHashTagDatas() }
    }

    private suspend fun observeCombinedPosts() {
        val assetPosts = repository.loadAssetsPosts(context)
        dao.getAllPosts().collectLatest { dbEntities ->
            val dbPosts = dbEntities.map { it.toCommunityPost() }
            _posts.value = assetPosts + dbPosts
        }
    }

    private suspend fun loadCategoryImages() {
        val images = repository.loadCategoryImages(context)
        _categoryImages.value = images
    }

    private suspend fun loadHashTagDatas() {
        val tags = repository.loadHashTagDatas(context)
        _hashTagDatas.value = tags
    }

    fun setPosts(newPosts: List<CommunityPost>) {
        _posts.value = newPosts
    }

    fun reloadPostsFromFile() {
        viewModelScope.launch {
            val newPosts = repository.loadPostsFromFile(context)
            setPosts(newPosts)
        }
    }

    fun loadChallengeDatas(): List<ChallengeData> {
        return repository.loadChallengeDatas(context)
    }
}

// 확장 함수
fun CommunityPostEntity.toCommunityPost() = CommunityPost(
    id = this.userId,
    pfp = this.pfp,
    intro = this.intro,
    title = this.title,
    post = this.post,
    postPic = this.postPic,
    genre = this.genre
)