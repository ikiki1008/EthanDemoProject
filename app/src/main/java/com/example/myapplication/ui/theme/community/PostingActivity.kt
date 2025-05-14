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
    var selectedCategory by remember { mutableStateOf("살까말까") }

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
                Text(text = "게시판 선택", fontSize = 20.sp, color = Color.Black)
            }

            CategoryItem("살까말까", "살지 말지 망설이는 상품이 있다면 고민을 나눠보세요") {
                selectedCategory = "살까말까"
                showSheet = false
            }

            CategoryItem("상품후기", "구매했거나 사용해본 상품의 솔직한 후기를 공유해보세요") {
                selectedCategory = "상품후기"
                showSheet = false
            }

            CategoryItem("꿀템수다", "사소한 고민부터 소소한 정보까지, 상품 관련 수다를 나눠보세요") {
                selectedCategory = "꿀템수다"
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
            text = "커뮤니티 글쓰기",
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
                text = "올리기",
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
            Text(text = selectedCategory, fontSize = 16.sp)
            Image(
                painter = painterResource(id = R.drawable.down_arrow),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        // 텍스트 입력란
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
fun InputField(category: String) {
    var title by remember {mutableStateOf(TextFieldValue(""))}
    var subtitle by remember {mutableStateOf(TextFieldValue(""))}
    val placeholderMap = mapOf(
        "살까말까" to "살지 말지 망설이는 상품이 있다면 고민을 나눠보세요",
        "상품후기" to "구매했거나 사용해본 상품의 솔직한 후기를 공유해보세요",
        "꿀템수다" to "사소한 고민부터 소소한 정보까지, 상품 관련 수다를 나눠보세요"
    )
    val subtitlePlaceHolder = placeholderMap[category] ?: ""

    Column (modifier = Modifier.padding(horizontal = 10.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = {title = it},
            placeholder = {Text("제목을 입력해주세요", fontSize = 20.sp, color = Color.LightGray)},
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = subtitle,
            onValueChange = {subtitle = it},
            placeholder = {Text(subtitlePlaceHolder, fontSize = 15.sp, color = Color.LightGray)},
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
    }
}
