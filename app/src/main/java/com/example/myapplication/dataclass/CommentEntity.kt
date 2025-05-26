package com.example.myapplication.dataclass

import androidx.compose.ui.res.stringResource
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val postId: String,
    val username: String = "프리렌_언제나오냐",
    val content: String,
    val timestamp : Long = System.currentTimeMillis() //time stamp
)
