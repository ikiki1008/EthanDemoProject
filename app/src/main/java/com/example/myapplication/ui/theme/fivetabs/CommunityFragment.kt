package com.example.myapplication.ui.theme.fivetabs

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition.Center
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.ui.theme.community.CommunityViewModel
import com.example.myapplication.ui.theme.community.PostDetailActivity
import com.example.myapplication.ui.theme.community.PostingActivity
import com.example.myapplication.ui.theme.dataclass.CommunityPost
import com.example.myapplication.ui.theme.home.HomeFeedViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.google.gson.Gson
import com.google.common.reflect.TypeToken
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import okhttp3.Challenge
import java.io.File

class CommunityFragment : Fragment() {

    private val viewModel: CommunityViewModel by viewModels()

    private val postingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // 새 글 작성 후 JSON 다시 읽어서 뷰모델에 세팅
            lifecycleScope.launch {
                val newPosts = loadPostsFromJason(requireContext())
                viewModel.setPosts(newPosts)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CommunityScreen(
                    viewModel = viewModel,
                    onPostClick = { openPostingActivity() }
                )
            }
        }
    }

    // 버튼 클릭 등에서 글 작성 화면 열기
    fun openPostingActivity() {
        val intent = Intent(requireContext(), PostingActivity::class.java)
        postingLauncher.launch(intent)
    }

    private suspend fun loadPostsFromJason(context: android.content.Context): List<CommunityPost> {
        val file = File(context.filesDir, "community_post.json")
        if (!file.exists()) return emptyList()

        val json = file.readText()
        val gson = Gson()
        val type = object : TypeToken<List<CommunityPost>>() {}.type
        return gson.fromJson(json, type)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = viewModel(),
    onPostClick: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val posts by viewModel.posts.collectAsState()

    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf(
        R.string.community_tab_1,
        R.string.community_tab_2,
        R.string.community_tab_3,
        R.string.community_tab_4
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val viewModel: CommunityViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application))
    val showShimmering = viewModel.showShimmering

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = pagerState.currentPage, containerColor = Color.White) {
                tabs.forEachIndexed { index, titleResId ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.scrollToPage(index) }
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
                    1 -> ChannelFeed(viewModel)
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
                                    onPostClick()
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
fun ItemFoundFeed(
    lazyListState: LazyListState,
    viewModel: CommunityViewModel = viewModel()
) {
    val stringIds = listOf(
        R.string.all,
        R.string.popular,
        R.string.buy_or_not,
        R.string.item_review,
        R.string.honey_item_comm
    )
    var selectedIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // ViewModel에서 posts 상태 받아오기
    val allPosts by viewModel.posts.collectAsState()

    val filteredPosts = when (selectedIndex) {
        2 -> allPosts.filter { it.genre == context.getString(R.string.buy_or_not) }
        3 -> allPosts.filter { it.genre == context.getString(R.string.item_review) }
        4 -> allPosts.filter { it.genre == context.getString(R.string.honey_item_comm) }
        else -> allPosts
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
                    putExtra("title", post.title)
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

                Text(
                    text = post.title,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = post.id,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            GlideImage(
                imageModel = post.postPic,
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
    val selectedBackgroundColor = colorResource(id=R.color.ohouse_color)

    val backgroundColor = if (isSelected) selectedBackgroundColor else Color.LightGray
    val contentColor = if (isSelected) Color.White else Color.White
    val borderColor = if (isSelected) Color.Transparent else Color.Transparent

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
fun ChannelFeed(viewModel: CommunityViewModel) {
    val challengeItems = remember { viewModel.loadChallengeDatas() }
    val hashTagItems by viewModel.hashTagDatas.collectAsState()
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState, modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = stringResource(R.string.challenge_channel),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Left,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        item {
            LazyRow {
                items(challengeItems.chunked(2)) { chunk ->
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(300.dp)
                    ) {
                        chunk.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GlideImage(
                                    imageModel = item.pic,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = item.subTitle,
                                        fontSize = 12.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.popular_channels_now),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Left,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(hashTagItems) { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.subText,
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(item.img1, item.img2, item.img3, item.img4).forEach { img ->
                        GlideImage(
                            imageModel = img,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 85.dp, height = 140.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun HouseVisitingFeed() {
    Text("This is house visiting feed")
}

@Composable
fun HousePicFeed() {
    Text("This is house pic feed")
}
