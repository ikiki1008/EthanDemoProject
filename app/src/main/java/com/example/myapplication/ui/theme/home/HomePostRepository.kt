package com.example.myapplication.ui.theme.home

import android.content.Context
import com.example.myapplication.domain.CreatorPost
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class HomePostRepository (private val context: Context) {
    fun loadPostFromAssets(): List<CreatorPost> {
        val json = context.assets.open("sample_data.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(json, object : TypeToken<List<CreatorPost>>() {}.type)
    }
}