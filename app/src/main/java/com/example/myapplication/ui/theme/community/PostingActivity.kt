package com.example.myapplication.ui.theme.community

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlinx.coroutines.launch

class PostingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostingScreen(onBack = { finish() }, onPost = {
                // 글쓰기 처리 로직
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostingScreen(onBack: () -> Unit, onPost: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(R.string.buy_or_not) }

    // 바텀 시트
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
                selectedCategory = R.string.buy_or_not
                showSheet = false
            }

            CategoryItem(stringResource(R.string.item_review), stringResource(R.string.review_post)) {
                selectedCategory = R.string.item_review
                showSheet = false
            }

            CategoryItem(stringResource(R.string.honey_item_comm), stringResource(R.string.honey_tip_post)) {
                selectedCategory = R.string.honey_item_comm
                showSheet = false
            }
        }
    }

    // 상단 바
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

    // 콘텐츠 영역
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
    ) {
        // 카테고리 탭
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

        InputField(selectedCategory)
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
fun InputField(category: Int) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var subtitle by remember { mutableStateOf(TextFieldValue("")) }
    val placeholderMap = mapOf(
        R.string.buy_or_not to R.string.buy_or_not_post,
        R.string.item_review to R.string.review_post,
        R.string.honey_item_comm to R.string.honey_tip_post
    )

    val subtitlePlaceHolder = placeholderMap[category] ?: R.string.buy_or_not_post

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text(stringResource(R.string.make_the_title), fontSize = 20.sp, color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = subtitle,
            onValueChange = { subtitle = it },
            placeholder = { Text(stringResource(id = subtitlePlaceHolder), fontSize = 15.sp, color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}