package com.example.myapplication.ui.theme.community

import android.R
import android.os.Bundle
import android.widget.Space
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.State
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon

class PostDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("title") ?: ""
        val id = intent.getStringExtra("postId") ?: ""
        val pfp = intent.getStringExtra("pfp") ?: ""
        val intro = intent.getStringExtra("intro") ?: ""
        val post = intent.getStringExtra("post") ?: ""
        val postPic = intent.getStringExtra("postPic") ?: ""
        val menu = intent.getStringExtra("postGenre") ?: ""

        val commentViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )
            .get(CommentViewModel::class.java)

        setContent {
            MyApplicationTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    PostingDetailScreen(
                        title = title,
                        id = id,
                        pfp = pfp,
                        intro = intro,
                        post = post,
                        postPic = postPic,
                        genre = menu,
                        onBackPressed = { ActivityCompat.finishAfterTransition(this@PostDetailActivity) }
                    )

                    Spacer(modifier = Modifier.height(2.dp)) // 여유 공간
                    PostingReplyScreen(postId = id, commentViewModel = commentViewModel)
                }
            }
        }
    }

    @Composable
    fun PostingDetailScreen(
        title: String,
        id: String,
        pfp: String,
        intro: String,
        post: String,
        postPic: String,
        genre: String,
        onBackPressed: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = com.example.myapplication.R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackPressed() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    Text(text = genre, fontSize = 14.sp, color = Color.DarkGray)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = pfp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = id,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = post,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (postPic.isNotEmpty()) {
                        AsyncImage(
                            model = postPic,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun PostingReplyScreen(postId: String, commentViewModel: CommentViewModel) {
        val comments by commentViewModel.comments.collectAsState()
        var input by remember { mutableStateOf("") }

        LaunchedEffect(postId) {
            commentViewModel.loadComments(postId)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // 댓글 입력창
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("댓글을 입력하세요") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "등록",
                    modifier = Modifier
                        .clickable {
                            if (input.isNotBlank()) {
                                commentViewModel.addComment(postId, input)
                                input = ""
                            }
                        }
                        .padding(8.dp),
                    color = Color.Blue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 댓글 목록 (삭제하기 기능 추가)
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = comments,
                    key = { it.id } // id는 CommentEntity에 있어야 함
                ) { comment ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                commentViewModel.deleteComment(comment)
                            }
                            true
                        }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            val color = when (dismissState.dismissDirection) {
                                DismissDirection.EndToStart -> Color.Red
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "삭제",
                                    tint = Color.White
                                )
                            }
                        },
                        dismissContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(Color.White)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = comment.username,
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                                Text(text = comment.content, fontSize = 16.sp, color = Color.Black)
                            }
                        }
                    )
                }
            }
        }
    }
}