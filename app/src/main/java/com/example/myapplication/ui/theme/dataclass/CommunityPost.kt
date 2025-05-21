package com.example.myapplication.ui.theme.dataclass

import com.example.myapplication.ui.theme.community.CommunityPostEntity

data class CommunityPost(
    val id : String,
    val pfp : String? = null,
    val intro : String? = null,
    val title: String,
    val post : String,
    val postPic : String? = null,
    val genre : String
)

fun CommunityPost.toEntity() = CommunityPostEntity(
    userId = this.id,
    pfp = this.pfp,
    intro = this.intro,
    title = this.title,
    post = this.post,
    postPic = this.postPic,
    genre = this.genre
)
