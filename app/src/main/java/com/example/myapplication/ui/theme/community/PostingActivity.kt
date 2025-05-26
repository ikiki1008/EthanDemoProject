package com.example.myapplication.ui.theme.community

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlinx.coroutines.launch
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import com.example.myapplication.dataclass.CommunityDataBase
import com.example.myapplication.domain.CommunityPost
import com.example.myapplication.domain.toEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PostingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = this
            var title by remember { mutableStateOf(TextFieldValue("")) }
            var subTitle by remember { mutableStateOf(TextFieldValue("")) }
            var selectedCategory by remember { mutableStateOf(R.string.buy_or_not) }

            val genre = stringResource(id = selectedCategory)

            PostingScreen(
                title = title,
                onTitleChange = { title = it },
                subTitle = subTitle,
                onSubTitleChange = { subTitle = it },
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                onBack = { finish() },
                onPost = {
                    if (title.text.isBlank() || subTitle.text.isBlank()) {
                        Toast.makeText(context, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@PostingScreen
                    }

                    val newPost = CommunityPost(
                        id = "프리렌 언제나오냥",
                        pfp = "https://image2.1004gundam.com/item_images/goods/380/1376415975.JPG",
                        intro = "오유경 1996년생 10월 8일 출신 호랑먀유~",
                        title = title.text,
                        post = subTitle.text,
                        postPic = null,
                        genre = genre
                    )

                    //db에 저장
                    val db = CommunityDataBase.getDataBase(context)
                    val dao = db.communityPostDao()

                    CoroutineScope(Dispatchers.IO).launch {
                        dao.insertPost(newPost.toEntity())
                        withContext(Dispatchers.Main) {
                            finish()
                        }
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostingScreen(
    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    subTitle: TextFieldValue,
    onSubTitleChange: (TextFieldValue) -> Unit,
    selectedCategory: Int,
    onCategoryChange: (Int) -> Unit,
    onBack: () -> Unit,
    onPost: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.choose_the_tags), fontSize = 20.sp, color = Color.Black)
            }

            CategoryItem(stringResource(R.string.buy_or_not), stringResource(R.string.buy_or_not_post)) {
                onCategoryChange(R.string.buy_or_not)
                showSheet = false
            }

            CategoryItem(stringResource(R.string.item_review), stringResource(R.string.review_post)) {
                onCategoryChange(R.string.item_review)
                showSheet = false
            }

            CategoryItem(stringResource(R.string.honey_item_comm), stringResource(R.string.honey_tip_post)) {
                onCategoryChange(R.string.honey_item_comm)
                showSheet = false
            }
        }
    }

    val genre = stringResource(id = selectedCategory)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(40.dp)
                .padding(start = 15.dp, top = 7.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = null
            )
        }

        Text(
            text = stringResource(R.string.post_on_community),
            fontSize = 17.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(13.dp)
        )

        TextButton(
            onClick = onPost,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 15.dp)
        ) {
            Text(
                text = stringResource(R.string.upload),
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(60.dp)
                .clickable {
                    coroutineScope.launch {
                        showSheet = true
                        sheetState.show()
                    }
                }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = selectedCategory), fontSize = 16.sp)
            Image(
                painter = painterResource(id = R.drawable.down_arrow),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        InputField(
            category = selectedCategory,
            title = title,
            onTitleChange = onTitleChange,
            subtitle = subTitle,
            onSubtitleChange = onSubTitleChange
        )
    }
}


@Composable
fun CategoryItem(title: String, description: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(text = title, fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(start = 10.dp))
        Text(text = description, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(start = 10.dp))
    }
}

@Composable
fun InputField(
    category: Int,
    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    subtitle: TextFieldValue,
    onSubtitleChange: (TextFieldValue) -> Unit
) {
    val placeholderMap = mapOf(
        R.string.buy_or_not to R.string.buy_or_not_post,
        R.string.item_review to R.string.review_post,
        R.string.honey_item_comm to R.string.honey_tip_post
    )

    val subtitlePlaceHolder = placeholderMap[category] ?: R.string.buy_or_not_post

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.make_the_title),
                    fontSize = 22.sp,
                    color = Color.LightGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 22.sp)
        )

        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        TextField(
            value = subtitle,
            onValueChange = onSubtitleChange,
            placeholder = {
                Text(
                    text = stringResource(id = subtitlePlaceHolder),
                    fontSize = 18.sp,
                    color = Color.LightGray
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 18.sp)
        )
    }
}

fun savePostToJason (context : Context, newPost : CommunityPost) {
    val gson = Gson()
    val file = File(context.filesDir, "community_post.json")

    //기존 데이터 불러오기
    val postList: MutableList<CommunityPost> = if (file.exists()) {
        val json = file.readText()
        val type = object : TypeToken<MutableList<CommunityPost>>() {}.type
        gson.fromJson(json, type)
    } else {
        mutableListOf()
    }

    postList.add(newPost) //신 데이터 추가
    file.writeText(gson.toJson(postList)) //덮어쓰기
}