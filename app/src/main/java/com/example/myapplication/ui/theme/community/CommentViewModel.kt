package com.example.myapplication.ui.theme.community

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.myapplication.MyApplication
import com.example.myapplication.dataclass.AppDataBase
import com.example.myapplication.dataclass.CommentEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(application: Application) : ViewModel() {

    private val db = Room.databaseBuilder(application, AppDataBase::class.java, "app-db").fallbackToDestructiveMigration().build()
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
            db.commentDao().insertComment(CommentEntity(postId = postId, content = content, timestamp = System.currentTimeMillis()))
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