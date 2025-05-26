package com.example.myapplication.ui.theme.community

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dataclass.CommunityDataBase
import com.example.myapplication.domain.CommunityPost
import com.example.myapplication.dataclass.CommunityPostEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

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

    //커뮤니티 게시물 상태
    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    //카테고리 이미지 상태
    private val _categoryImages = MutableStateFlow<List<CategoryImage>>(emptyList())
    val categoryImages: StateFlow<List<CategoryImage>> = _categoryImages.asStateFlow()

    //해쉬태그 데이터들
    private val _hashTagDatas = MutableStateFlow<List<HashTagData>>(emptyList())
    val hashTagDatas: StateFlow<List<HashTagData>> = _hashTagDatas.asStateFlow()

    val showShimmering = mutableStateOf(true)

    init {
        viewModelScope.launch {
            observeCombinedPosts()
        }
        viewModelScope.launch {
            loadCategoryImages()
        }
        viewModelScope.launch {
            loadHashTagDatas()
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

    private suspend fun loadHashTagDatas() {
        val context = getApplication<Application>()

        val result = withContext(Dispatchers.IO) {
            try {
                val jsonString = context.assets.open("popular_channels_now.json")
                    .bufferedReader()
                    .use { it.readText() }

                val jsonArray = JSONArray(jsonString)
                List(jsonArray.length()) { i ->
                    val obj = jsonArray.getJSONObject(i)
                    HashTagData(
                        title = obj.getString("hashtag"),
                        subText = obj.getString("subtitle"),
                        img1 = obj.getString("img1"),
                        img2 = obj.getString("img2"),
                        img3 = obj.getString("img3"),
                        img4 = obj.getString("img4")
                    )
                }
            } catch (e: Exception) {
                Log.e("CommunityViewModel", "Failed to load hashtag data", e)
                emptyList()
            }
        }

        _hashTagDatas.value = result
    }

    // 카테고리 이미지 JSON 로드 함수
    private suspend fun loadCategoryImages() {
        val context = getApplication<Application>()
        val result = withContext(Dispatchers.IO) {
            try {
                val jsonString = context.assets.open("category_images.json")
                    .bufferedReader()
                    .use { it.readText() }

                val jsonArray = JSONArray(jsonString)
                List(jsonArray.length()) { i ->
                    val obj = jsonArray.getJSONObject(i)
                    CategoryImage(
                        pic = obj.getString("pic"), title = obj.getString("text"),
                        subTitle = obj.getString("subtext")
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
        _categoryImages.value = result
    }

    fun setPosts(newPosts: List<CommunityPost>) {
        _posts.value = newPosts
    }

    fun loadChallengeDatas(): List<ChallengeData> {
        val context = getApplication<Application>()
        return try {
            val jsonString = context.assets.open("channel_feed_image_item.json")
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(jsonString)
            List(jsonArray.length()) { i ->
                val obj = jsonArray.getJSONObject(i)
                ChallengeData(
                    pic = obj.getString("pic"),
                    title = obj.getString("text"),
                    subTitle = obj.getString("subtext")
                )
            }.also {
                Log.d("CommunityViewModel", "Loaded ${it.size} challenge items")
            }
        } catch (e: Exception) {
            Log.e("CommunityViewModel", "Failed to load challenge data", e)
            emptyList()
        }
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
