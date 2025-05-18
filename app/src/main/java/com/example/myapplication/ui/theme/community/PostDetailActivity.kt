package com.example.myapplication.ui.theme.community

import android.R
import android.os.Bundle
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme

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
                    PostingReplyScreen()
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

    @Composable
    fun PostingReplyScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.VideoView_error_button),
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}