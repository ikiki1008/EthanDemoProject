package com.example.myapplication.ui.theme.community

import androidx.compose.ui.input.pointer.PointerId
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.ui.theme.dataclass.CommentEntity

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment (comment : CommentEntity)

    @Query("SELECT * FROM comments WHERE postId = :postId")
    suspend fun getCommentForPost(postId: String): List<CommentEntity>

    @Delete
    suspend fun deleteComment(comment: CommentEntity)
}