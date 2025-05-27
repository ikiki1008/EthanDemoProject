package com.example.myapplication.repository

import android.content.Context
import com.example.myapplication.domain.CommunityPost
import com.example.myapplication.ui.theme.community.CategoryImage
import com.example.myapplication.ui.theme.community.ChallengeData
import com.example.myapplication.ui.theme.community.HashTagData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File

class CommunityRepository {

    suspend fun loadAssetsPosts(context: Context): List<CommunityPost> = withContext(Dispatchers.IO) {
        val json = context.assets.open("community_post.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<CommunityPost>>() {}.type
        Gson().fromJson(json,type)
    }

    suspend fun loadPostsFromFile(context: Context): List<CommunityPost> = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, "community_post.json")
        if (!file.exists()) return@withContext emptyList()
        val json = file.readText()
        val type = object : TypeToken<List<CommunityPost>>() {}.type
        Gson().fromJson(json, type)
    }

    suspend fun loadCategoryImages(context: Context): List<CategoryImage> = withContext(Dispatchers.IO) {
        try {
            val json = context.assets.open("category_images.json")
                .bufferedReader().use { it.readText() }
            val array = JSONArray(json)
            List(array.length()) {
                val obj = array.getJSONObject(it)
                CategoryImage(
                    pic = obj.getString("pic"),
                    title = obj.getString("text"),
                    subTitle = obj.getString("subtext")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun loadHashTagDatas(context: Context): List<HashTagData> = withContext(Dispatchers.IO) {
        try {
            val json = context.assets.open("popular_channels_now.json")
                .bufferedReader().use { it.readText() }
            val array = JSONArray(json)
            List(array.length()) {
                val obj = array.getJSONObject(it)
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
            emptyList()
        }
    }

    fun loadChallengeDatas(context: Context): List<ChallengeData> {
        return try {
            val json = context.assets.open("channel_feed_image_item.json")
                .bufferedReader().use { it.readText() }
            val array = JSONArray(json)
            List(array.length()) {
                val obj = array.getJSONObject(it)
                ChallengeData(
                    pic = obj.getString("pic"),
                    title = obj.getString("text"),
                    subTitle = obj.getString("subtext")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}