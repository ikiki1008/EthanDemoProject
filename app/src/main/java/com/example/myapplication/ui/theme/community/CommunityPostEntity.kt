package com.example.myapplication.ui.theme.community

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val pfp: String?,
    val intro: String?,
    val title: String,
    val post: String,
    val postPic: String?,
    val genre: String
)
