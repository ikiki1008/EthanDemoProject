package com.example.myapplication.ui.theme.fivetabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.ui.theme.community.PostDetailActivity
import com.example.myapplication.ui.theme.community.PostingActivity
import com.example.myapplication.ui.theme.dataclass.CommunityPost
import com.google.gson.Gson
import com.google.common.reflect.TypeToken
import kotlinx.coroutines.launch
import kotlin.jvm.java

class CommunityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CommunityScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommunityScreen() {
    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0 }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val tabs = listOf(
        R.string.community_tab_1,
        R.string.community_tab_2,
        R.string.community_tab_3,
        R.string.community_tab_4
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, titleResId ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(id = titleResId),
                                color = if (pagerState.currentPage == index) Color.Black else Color.Gray
                            )
                        }
                    )
                }
            }

            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> ItemFoundFeed(lazyListState)
                    1 -> ChannelFeed()
                    2 -> HouseVisitingFeed()
                    3 -> HousePicFeed()
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 20.dp)
        ) {
            Button(
                onClick = { showBottomSheet = true },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                modifier = Modifier.height(50.dp)
            ) {
                AnimatedContent(targetState = isAtTop, label = "FAB") { top ->
                    Text(
                        text = stringResource(id = if (top) R.string.post_on else R.string.post_off),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                val items = listOf(R.string.picture, R.string.video, R.string.community)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(items.size) { index ->
                        val label = items[index]
                        OutlinedButton(
                            onClick = {
                                showBottomSheet = false
                                if (label == R.string.community) {
                                    context.startActivity(Intent(context, PostingActivity::class.java))
                                }
                            },
                            modifier = Modifier.padding(end = 12.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(text = stringResource(id = label))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ItemFoundFeed(lazyListState: LazyListState) {
    val stringIds = listOf(
        R.string.all,
        R.string.popular,
        R.string.buy_or_not,
        R.string.item_review,
        R.string.honey_item_comm
    )
    var selectedIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val allJsonPosts by produceState<List<CommunityPost>>(initialValue = emptyList(), key1 = context) {
        val json = context.assets.open("community_post.json")
            .bufferedReader().use { it.readText() }
        value = Gson().fromJson(json, object : TypeToken<List<CommunityPost>>() {}.type)
    }
    //filter 를 사용해서 포스트를 정렬한다
    val filteredPosts = when (selectedIndex) {
        2 -> allJsonPosts.filter { it.genre == stringResource(R.string.buy_or_not) }
        3 -> allJsonPosts.filter { it.genre == stringResource(R.string.item_review) }
        4 -> allJsonPosts.filter { it.genre == stringResource(R.string.honey_item_comm) }
        else -> allJsonPosts
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                itemsIndexed(stringIds) { index, id ->
                    val title = stringResource(id = id)

                    RoundedTabButton(
                        title = title,
                        isSelected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        items(filteredPosts) { post ->
            PostCardShape(post = post)
        }
    }
}

@Composable
fun PostCardShape(post: CommunityPost) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                val intent = Intent(context, PostDetailActivity::class.java).apply {
                    putExtra("postId", post.id)
                    putExtra("title", R.string.buy_or_not)
                    putExtra("pfp", post.pfp)
                    putExtra("intro", post.intro)
                    putExtra("post", post.post)
                    putExtra("postPic", post.postPic)
                    putExtra("postGenre", post.genre)
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(contentColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 장르 박스
                Box(
                    modifier = Modifier
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = post.genre,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        color = Color.DarkGray
                    )
                }

                // 제목
                Text(
                    text = stringResource(R.string.make_the_title),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // 사용자 ID
                Text(
                    text = post.id,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            AsyncImage(
                model = post.postPic,
                contentDescription = null,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(10))
            )
        }
    }
}


@Composable
fun RoundedTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF0D1A45) else Color.White
    val contentColor = if (isSelected) Color.White else Color.Black
    val borderColor = if (isSelected) Color(0xFF0D1A45) else Color.LightGray

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Thin),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChannelFeed() {
    Text("This is channel feed")
}

@Composable
fun HouseVisitingFeed() {
    Text("This is house visiting feed")
}

@Composable
fun HousePicFeed() {
    Text("This is house pic feed")
}



