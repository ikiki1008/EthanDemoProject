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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import javax.inject.Inject

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

@HiltViewModel
class CommunityViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

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
        viewModelScope.launch {
            showShimmering.value = true
            try {
                val assetPosts = repository.loadAssetsPosts(context)
                val dbPostsFlow = dao.getAllPosts()
                val categoryImages = repository.loadCategoryImages(context)
                val hashTagData = repository.loadHashTagDatas(context)

                launch { observeDbAndAssetPosts(assetPosts, dbPostsFlow) }
                _categoryImages.value = categoryImages
                _hashTagDatas.value = hashTagData

                kotlinx.coroutines.delay(2000) //쉬머링 효과를 보여주기 위한 딜레이 시간
            } catch (e: Exception) {
                Log.e("TAG", "초기화 중 오류: ${e.printStackTrace()}" )
            } finally {
                showShimmering.value = false
            }
        }
    }

    private fun observeDbAndAssetPosts(
        assetPosts: List<CommunityPost>,
        dbPostsFlow: Flow<List<CommunityPostEntity>>
    ) {
        viewModelScope.launch {
            dbPostsFlow.collectLatest { dbEntities ->
                val dbPosts = dbEntities.map { it.toCommunityPost() }
                _posts.value = assetPosts + dbPosts
            }
        }
    }

    fun setPosts(newPosts: List<CommunityPost>) {
        _posts.value = newPosts
    }

    fun reloadPostsFromFile() {
        viewModelScope.launch {
            showShimmering.value = true
            try {
                val newPosts = repository.loadPostsFromFile(context)
                setPosts(newPosts)
            } finally {
                showShimmering.value = false
            }
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