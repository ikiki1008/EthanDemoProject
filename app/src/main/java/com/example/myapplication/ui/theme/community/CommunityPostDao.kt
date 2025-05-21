package com.example.myapplication.ui.theme.community

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.ui.theme.dataclass.CommunityPost
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityPostDao {

    @Query("SELECT * FROM community_posts ORDER BY id DESC")
    fun getAllPosts(): Flow<List<CommunityPostEntity>>

    @Query("SELECT * FROM community_posts ORDER BY id DESC")
    suspend fun getAllPostsOnce(): List<CommunityPostEntity>

    @Insert
    suspend fun insertPost(post: CommunityPostEntity)
}
