package com.example.myapplication.ui.theme.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.myapplication.ui.theme.dataclass.CommentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, AppDataBase::class.java, "app-db").build()
    private val _comments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val comments = _comments.asStateFlow()

    //댓글 불러오기
    fun loadComments(postId:String) {
        viewModelScope.launch {
            _comments.value = db.commentDao().getCommentForPost(postId)
        }
    }

    //댓글 추가하기
    fun addComment(postId: String, content:String) {
        viewModelScope.launch {
            db.commentDao().insertComment(CommentEntity(postId = postId, content = content))
            loadComments(postId)
        }
    }

    //댓글 삭제하기
    fun deleteComment(comment : CommentEntity) {
        viewModelScope.launch {
            db.commentDao().deleteComment(comment)
            loadComments(comment.postId)
        }
    }
}