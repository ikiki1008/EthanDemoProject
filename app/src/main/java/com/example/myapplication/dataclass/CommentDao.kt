package com.example.myapplication.dataclass

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment (comment : CommentEntity)

    @Query("SELECT * FROM comments WHERE postId = :postId")
    suspend fun getCommentForPost(postId: String): List<CommentEntity>

    @Delete
    suspend fun deleteComment(comment: CommentEntity)
}