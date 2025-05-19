package com.example.myapplication.ui.theme.dataclass

data class CommunityPost(
    val id : String,
    val pfp : String? = null,
    val intro : String,
    val title: String,
    val post : String,
    val postPic : String? = null,
    val genre : String
)
